package com.nookx.api.scraper.core.runner;

import com.nookx.api.config.ApplicationProperties;
import com.nookx.api.scraper.core.service.AssetDownloadService;
import com.nookx.api.scraper.domain.SourceSetAsset;
import com.nookx.api.scraper.repository.SourceSetAssetRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Picks one pending {@link SourceSetAsset} per tick and delegates the actual download to
 * {@link AssetDownloadService} (which owns its own transactional boundary).
 * <p>
 * Runs at {@code fetchDelayMs} with {@code fetchJitterMs} jitter, matching the 1 req/min policy
 * agreed for the playmodb.org domain. The pick is performed outside of any transaction so that a
 * slow HTTP download does not keep a DB transaction open.
 */
@Component
@Profile("scraper")
public class AssetFetchRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AssetFetchRunner.class);

    private final SourceSetAssetRepository sourceSetAssetRepository;
    private final AssetDownloadService assetDownloadService;
    private final ApplicationProperties applicationProperties;

    public AssetFetchRunner(
        SourceSetAssetRepository sourceSetAssetRepository,
        AssetDownloadService assetDownloadService,
        ApplicationProperties applicationProperties
    ) {
        this.sourceSetAssetRepository = sourceSetAssetRepository;
        this.assetDownloadService = assetDownloadService;
        this.applicationProperties = applicationProperties;
    }

    @Scheduled(
        fixedDelayString = "${application.scraper.asset-fetch-delay-ms:60000}",
        initialDelayString = "${application.scraper.asset-fetch-initial-delay-ms:60000}"
    )
    public void tick() {
        try {
            long sleep = ScraperJitter.pickJitter(applicationProperties.getScraper().getFetchJitterMs());
            if (sleep > 0) {
                Thread.sleep(sleep);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        runOnce();
    }

    public boolean runOnce() {
        Optional<SourceSetAsset> next = pickNext();
        if (next.isEmpty()) {
            LOG.debug("AssetFetchRunner tick: nothing to download");
            return false;
        }
        assetDownloadService.downloadOne(next.get().getId());
        return true;
    }

    private Optional<SourceSetAsset> pickNext() {
        List<SourceSetAsset> pending = sourceSetAssetRepository.findByDownloadedFalseAndDownloadFailedFalse(PageRequest.of(0, 1));
        return pending.stream().findFirst();
    }
}
