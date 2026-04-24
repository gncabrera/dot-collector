package com.nookx.api.scraper.core.service;

import com.nookx.api.domain.Interest;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.MegaSetType;
import com.nookx.api.repository.MegaSetRepository;
import com.nookx.api.scraper.api.dto.NormalizedAssetDto;
import com.nookx.api.scraper.api.dto.NormalizedSetDto;
import com.nookx.api.scraper.domain.MegaSetSourceLink;
import com.nookx.api.scraper.domain.ScrapePage;
import com.nookx.api.scraper.domain.SourceSet;
import com.nookx.api.scraper.domain.SourceSetAsset;
import com.nookx.api.scraper.repository.MegaSetSourceLinkRepository;
import com.nookx.api.scraper.repository.SourceSetAssetRepository;
import com.nookx.api.scraper.repository.SourceSetRepository;
import com.nookx.api.service.InterestService;
import com.nookx.api.service.MegaSetTypeService;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Maps a {@link NormalizedSetDto} produced by a parser to the staging + canonical tables.
 * <p>
 * Responsibilities:
 * <ol>
 *     <li>Upserts a {@link SourceSet} row for {@code (source_code, source_external_id)}.</li>
 *     <li>Upserts a {@link SourceSetAsset} row per referenced image / PDF (download happens later).</li>
 *     <li>Resolves or creates the canonical {@link MegaSet} and records the origin in
 *         {@link MegaSetSourceLink}. For the first source this is effectively a 1:1 mapping; when
 *         additional sources appear we will introduce a proper merge strategy.</li>
 * </ol>
 */
@Service
@Profile("scraper")
@Transactional
public class SourceIngestService {

    private static final Logger LOG = LoggerFactory.getLogger(SourceIngestService.class);

    private final SourceSetRepository sourceSetRepository;
    private final SourceSetAssetRepository sourceSetAssetRepository;
    private final MegaSetSourceLinkRepository megaSetSourceLinkRepository;
    private final MegaSetRepository megaSetRepository;
    private final InterestService interestService;
    private final MegaSetTypeService megaSetTypeService;

    public SourceIngestService(
        SourceSetRepository sourceSetRepository,
        SourceSetAssetRepository sourceSetAssetRepository,
        MegaSetSourceLinkRepository megaSetSourceLinkRepository,
        MegaSetRepository megaSetRepository,
        InterestService interestService,
        MegaSetTypeService megaSetTypeService
    ) {
        this.sourceSetRepository = sourceSetRepository;
        this.sourceSetAssetRepository = sourceSetAssetRepository;
        this.megaSetSourceLinkRepository = megaSetSourceLinkRepository;
        this.megaSetRepository = megaSetRepository;
        this.interestService = interestService;
        this.megaSetTypeService = megaSetTypeService;
    }

    public SourceSet ingest(NormalizedSetDto dto, ScrapePage scrapePage) {
        SourceSet sourceSet = sourceSetRepository
            .findBySourceCodeAndSourceExternalId(dto.sourceCode(), dto.sourceExternalId())
            .orElseGet(SourceSet::new);

        boolean isNew = sourceSet.getId() == null;
        sourceSet.setSourceCode(dto.sourceCode());
        sourceSet.setSourceExternalId(dto.sourceExternalId());
        sourceSet.setSetNumber(dto.setNumber() != null ? dto.setNumber() : dto.sourceExternalId());
        sourceSet.setName(dto.name());
        sourceSet.setDescription(dto.description());
        sourceSet.setReleaseDate(dto.releaseDate());
        sourceSet.setTheme(dto.theme());
        sourceSet.setRawAttributes(dto.rawAttributes());
        sourceSet.setScrapePage(scrapePage);
        sourceSet.setLastParsedAt(Instant.now());
        sourceSetRepository.save(sourceSet);

        for (NormalizedAssetDto asset : dto.assets()) {
            upsertAsset(sourceSet, asset);
        }

        MegaSet canonical = resolveCanonical(dto);
        updateCanonical(canonical, dto);
        megaSetRepository.save(canonical);
        linkCanonical(canonical, dto);

        LOG.debug("Ingested {}/{} (new={}, assets={})", dto.sourceCode(), dto.sourceExternalId(), isNew, dto.assets().size());
        return sourceSet;
    }

    private void upsertAsset(SourceSet sourceSet, NormalizedAssetDto asset) {
        Optional<SourceSetAsset> existing = sourceSetAssetRepository.findBySourceSetAndExternalUrl(sourceSet, asset.externalUrl());
        SourceSetAsset row = existing.orElseGet(SourceSetAsset::new);
        row.setSourceSet(sourceSet);
        row.setKind(asset.kind());
        row.setExternalUrl(asset.externalUrl());
        row.setLabel(asset.label());
        row.setSortOrder(asset.sortOrder());
        if (existing.isEmpty()) {
            row.setDownloaded(false);
            row.setDownloadFailed(false);
            row.setRetryCount(0);
        }
        sourceSetAssetRepository.save(row);
    }

    private MegaSet resolveCanonical(NormalizedSetDto dto) {
        Optional<MegaSetSourceLink> link = megaSetSourceLinkRepository.findBySourceCodeAndSourceExternalId(
            dto.sourceCode(),
            dto.sourceExternalId()
        );
        if (link.isPresent()) {
            return link.get().getMegaSet();
        }
        MegaSet fresh = new MegaSet();
        fresh.setSetNumber(buildCanonicalSetNumber(dto));
        return fresh;
    }

    private void updateCanonical(MegaSet canonical, NormalizedSetDto dto) {
        if (dto.name() != null && !dto.name().isBlank()) {
            canonical.setName(dto.name());
        } else if (canonical.getName() == null || canonical.getName().isBlank()) {
            canonical.setName(canonical.getSetNumber());
        }
        if (dto.description() != null) {
            canonical.setDescription(truncate(dto.description(), 255));
        }
        if (dto.releaseDate() != null) {
            canonical.setReleaseDate(dto.releaseDate());
        }
        if (dto.rawAttributes() != null) {
            canonical.setAttributes(dto.rawAttributes());
            canonical.setAttributesContentType("application/json");
        }
        attachInterestAndType(canonical, dto);
    }

    /**
     * Pins the canonical set to the catalog Interest declared by the source plugin (e.g. Klickypedia
     * → "Playmobil") and to the latest {@link MegaSetType} schema for that interest. Lookup is
     * best-effort: a missing interest only logs a warning so ingestion never fails on metadata.
     */
    private void attachInterestAndType(MegaSet canonical, NormalizedSetDto dto) {
        String interestName = dto.interestName();
        if (interestName == null || interestName.isBlank()) {
            return;
        }
        Optional<Interest> interestOpt = interestService.findByName(interestName);
        if (interestOpt.isEmpty()) {
            LOG.warn(
                "Interest '{}' declared by {}/{} was not found; canonical set will not be pinned",
                interestName,
                dto.sourceCode(),
                dto.sourceExternalId()
            );
            return;
        }
        Interest interest = interestOpt.get();
        canonical.setInterest(interest);

        MegaSetType currentType = interest.getSetType();
        if (currentType == null) {
            LOG.warn("Interest '{}' has no set type configured; cannot resolve latest schema", interestName);
            return;
        }
        Optional<MegaSetType> latestType = megaSetTypeService.findLatestEntityByName(currentType.getName());
        if (latestType.isPresent()) {
            canonical.setType(latestType.get());
        } else {
            LOG.warn("No latest MegaSetType found for name '{}'; falling back to interest's pinned type", currentType.getName());
            canonical.setType(currentType);
        }
    }

    private void linkCanonical(MegaSet canonical, NormalizedSetDto dto) {
        MegaSetSourceLink link = megaSetSourceLinkRepository
            .findBySourceCodeAndSourceExternalId(dto.sourceCode(), dto.sourceExternalId())
            .orElseGet(MegaSetSourceLink::new);
        link.setMegaSet(canonical);
        link.setSourceCode(dto.sourceCode());
        link.setSourceExternalId(dto.sourceExternalId());
        link.setLastMergedAt(Instant.now());
        if (link.getFirstMergedAt() == null) {
            link.setFirstMergedAt(Instant.now());
        }
        megaSetSourceLinkRepository.save(link);
    }

    private static String buildCanonicalSetNumber(NormalizedSetDto dto) {
        if (dto.setNumber() != null && !dto.setNumber().isBlank()) {
            return dto.sourceCode() + ":" + dto.setNumber();
        }
        return dto.sourceCode() + ":" + dto.sourceExternalId();
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
