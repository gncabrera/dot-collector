package com.nookx.api.scraper.core.service;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.MegaSetFile;
import com.nookx.api.domain.MegaSetImage;
import com.nookx.api.repository.MegaSetFileRepository;
import com.nookx.api.repository.MegaSetImageRepository;
import com.nookx.api.scraper.core.FetchResult;
import com.nookx.api.scraper.core.ScraperHttpClient;
import com.nookx.api.scraper.domain.MegaSetSourceLink;
import com.nookx.api.scraper.domain.SourceSet;
import com.nookx.api.scraper.domain.SourceSetAsset;
import com.nookx.api.scraper.domain.enumeration.SourceAssetKind;
import com.nookx.api.scraper.repository.MegaSetSourceLinkRepository;
import com.nookx.api.scraper.repository.SourceSetAssetRepository;
import com.nookx.api.service.MegaAssetService;
import java.net.URI;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Downloads one {@link SourceSetAsset} and links it to the canonical {@code MegaSet}.
 * <p>
 * Extracted from {@code AssetFetchRunner} so the {@code @Transactional} boundary is applied via
 * Spring's proxy (the runner calls {@link #downloadOne(SourceSetAsset)} from a different bean).
 */
@Service
@Profile("scraper")
public class AssetDownloadService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetDownloadService.class);
    private static final int MAX_ASSET_RETRIES = 5;

    private final SourceSetAssetRepository sourceSetAssetRepository;
    private final MegaSetSourceLinkRepository megaSetSourceLinkRepository;
    private final MegaSetImageRepository megaSetImageRepository;
    private final MegaSetFileRepository megaSetFileRepository;
    private final ScraperHttpClient httpClient;
    private final MegaAssetService megaAssetService;

    public AssetDownloadService(
        SourceSetAssetRepository sourceSetAssetRepository,
        MegaSetSourceLinkRepository megaSetSourceLinkRepository,
        MegaSetImageRepository megaSetImageRepository,
        MegaSetFileRepository megaSetFileRepository,
        ScraperHttpClient httpClient,
        MegaAssetService megaAssetService
    ) {
        this.sourceSetAssetRepository = sourceSetAssetRepository;
        this.megaSetSourceLinkRepository = megaSetSourceLinkRepository;
        this.megaSetImageRepository = megaSetImageRepository;
        this.megaSetFileRepository = megaSetFileRepository;
        this.httpClient = httpClient;
        this.megaAssetService = megaAssetService;
    }

    @Transactional
    public void downloadOne(SourceSetAsset asset) {
        LOG.info("Downloading asset {} ({}) for source set {}", asset.getExternalUrl(), asset.getKind(), asset.getSourceSet().getId());
        asset.setLastTriedAt(Instant.now());

        FetchResult result = httpClient.get(asset.getExternalUrl());

        if (result.isTransportError() || result.isTransient()) {
            onTransientFailure(asset, result);
            return;
        }
        if (result.isNotFound()) {
            asset.setDownloadFailed(true);
            asset.setLastError("HTTP 404");
            sourceSetAssetRepository.save(asset);
            return;
        }
        if (!result.isOk()) {
            onTransientFailure(asset, result);
            return;
        }

        byte[] bytes = result.body();
        String contentHash = sha256(bytes);
        String contentType = result.contentType() != null ? result.contentType() : inferContentType(asset);
        String filename = buildFilename(asset, contentType);

        MegaAsset megaAsset = megaAssetService.storeFromBytes(
            bytes,
            filename,
            contentType,
            "Imported from " + asset.getSourceSet().getSourceCode(),
            true,
            null
        );
        asset.setMegaAsset(megaAsset);
        asset.setDownloaded(true);
        asset.setDownloadFailed(false);
        asset.setDownloadedAt(Instant.now());
        asset.setContentHash(contentHash);
        asset.setContentSizeBytes((long) bytes.length);
        asset.setContentType(contentType);
        asset.setLastError(null);
        sourceSetAssetRepository.save(asset);

        linkToMegaSet(asset, megaAsset);
    }

    private void onTransientFailure(SourceSetAsset asset, FetchResult result) {
        asset.setRetryCount(asset.getRetryCount() + 1);
        asset.setLastError(result.errorMessage() != null ? result.errorMessage() : "HTTP " + result.status());
        if (asset.getRetryCount() >= MAX_ASSET_RETRIES) {
            asset.setDownloadFailed(true);
        }
        sourceSetAssetRepository.save(asset);
    }

    private void linkToMegaSet(SourceSetAsset asset, MegaAsset megaAsset) {
        SourceSet sourceSet = asset.getSourceSet();
        Optional<MegaSetSourceLink> link = megaSetSourceLinkRepository.findBySourceCodeAndSourceExternalId(
            sourceSet.getSourceCode(),
            sourceSet.getSourceExternalId()
        );
        if (link.isEmpty()) {
            LOG.warn(
                "No canonical MegaSet linked yet for {}/{}; asset stored but not linked",
                sourceSet.getSourceCode(),
                sourceSet.getSourceExternalId()
            );
            return;
        }
        MegaSet megaSet = link.get().getMegaSet();
        if (asset.getKind() == SourceAssetKind.IMAGE) {
            MegaSetImage row = new MegaSetImage();
            row.setMegaSet(megaSet);
            row.setAsset(megaAsset);
            row.setSortOrder(asset.getSortOrder() != null ? asset.getSortOrder() : 0);
            row.setLabel(asset.getLabel());
            megaSetImageRepository.save(row);
        } else {
            MegaSetFile row = new MegaSetFile();
            row.setMegaSet(megaSet);
            row.setAsset(megaAsset);
            row.setSortOrder(asset.getSortOrder() != null ? asset.getSortOrder() : 0);
            row.setLabel(asset.getLabel());
            megaSetFileRepository.save(row);
        }
    }

    private static String buildFilename(SourceSetAsset asset, String contentType) {
        String path = URI.create(asset.getExternalUrl()).getPath();
        String tail = path != null && path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
        if (tail == null || tail.isBlank()) {
            tail = "asset";
        }
        if (!tail.contains(".")) {
            tail = tail + guessExtension(contentType, asset.getKind());
        }
        return tail;
    }

    private static String guessExtension(String contentType, SourceAssetKind kind) {
        if (contentType != null) {
            String ct = contentType.toLowerCase(Locale.ROOT);
            if (ct.startsWith("image/jpeg")) return ".jpg";
            if (ct.startsWith("image/png")) return ".png";
            if (ct.startsWith("image/webp")) return ".webp";
            if (ct.startsWith("image/gif")) return ".gif";
            if (ct.startsWith("application/pdf")) return ".pdf";
        }
        return kind == SourceAssetKind.INSTRUCTIONS_PDF ? ".pdf" : ".bin";
    }

    private static String inferContentType(SourceSetAsset asset) {
        return asset.getKind() == SourceAssetKind.INSTRUCTIONS_PDF ? "application/pdf" : "application/octet-stream";
    }

    private static String sha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(bytes));
        } catch (Exception e) {
            return null;
        }
    }
}
