package com.nookx.api.scraper.core.runner;

import com.nookx.api.config.ApplicationProperties;
import com.nookx.api.scraper.api.CatalogSource;
import com.nookx.api.scraper.core.FetchResult;
import com.nookx.api.scraper.core.ScraperHttpClient;
import com.nookx.api.scraper.core.service.ScrapePageService;
import com.nookx.api.scraper.core.store.RawContentStore;
import com.nookx.api.scraper.domain.ScrapePage;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Fetches one due {@link ScrapePage} per tick.
 * <p>
 * Rate limiting: tick every {@code fetchDelayMs} with {@code fetchJitterMs} random offset. Because
 * the runner is single-threaded and picks at most one page per tick, the effective request rate
 * against the source never exceeds 1 / fetchDelayMs.
 * <p>
 * We rotate across enabled sources on every tick so that one very active source does not starve
 * the others.
 */
@Component
@Profile("scraper")
public class FetchRunner {

    private static final Logger LOG = LoggerFactory.getLogger(FetchRunner.class);

    private final List<CatalogSource> sources;
    private final ScrapePageService scrapePageService;
    private final ScraperHttpClient httpClient;
    private final RawContentStore rawContentStore;
    private final ApplicationProperties applicationProperties;

    public FetchRunner(
        List<CatalogSource> sources,
        ScrapePageService scrapePageService,
        ScraperHttpClient httpClient,
        RawContentStore rawContentStore,
        ApplicationProperties applicationProperties
    ) {
        this.sources = sources;
        this.scrapePageService = scrapePageService;
        this.httpClient = httpClient;
        this.rawContentStore = rawContentStore;
        this.applicationProperties = applicationProperties;
    }

    @Scheduled(
        fixedDelayString = "${application.scraper.fetch-delay-ms:15000}",
        initialDelayString = "${application.scraper.fetch-initial-delay-ms:15000}"
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

    /**
     * Fetches up to one due page across all enabled sources. Returns {@code true} if a page was
     * fetched (or a transport error recorded), {@code false} if nothing was due.
     */
    public boolean runOnce() {
        for (CatalogSource source : pickRotation()) {
            if (!isSourceEnabled(source)) {
                continue;
            }
            Optional<ScrapePage> next = scrapePageService.pickNextDue(source.sourceCode());
            if (next.isPresent()) {
                handle(next.get());
                return true;
            }
        }
        LOG.debug("FetchRunner tick: nothing due");
        return false;
    }

    private void handle(ScrapePage page) {
        LOG.info("Fetching {} {} [{}]", page.getSourceCode(), page.getPageType(), page.getUrl());
        FetchResult result = httpClient.get(page.getUrl(), page.getEtag(), page.getLastModified());

        if (result.isTransportError()) {
            scrapePageService.markFetchTransient(page, result.errorMessage());
            return;
        }
        if (result.isNotModified()) {
            scrapePageService.markFetchNotModified(page, null);
            return;
        }
        if (result.isNotFound()) {
            scrapePageService.markFetchNotFound(page);
            return;
        }
        if (result.isTransient()) {
            scrapePageService.markFetchTransient(page, "HTTP " + result.status());
            return;
        }
        if (!result.isOk()) {
            scrapePageService.markFetchTransient(page, "Unexpected HTTP " + result.status());
            return;
        }

        String contentHash = sha256(result.body());
        String storagePath = rawContentStore.store(
            page.getSourceCode(),
            page.getPageType(),
            page.getNaturalKey(),
            String.valueOf(page.getId()),
            result.body()
        );
        scrapePageService.markFetchSuccess(page, result, storagePath, contentHash, null);
    }

    private List<CatalogSource> pickRotation() {
        if (sources.isEmpty()) {
            return List.of();
        }
        int pivot = (int) (System.currentTimeMillis() % sources.size());
        ArrayList<CatalogSource> rotated = new ArrayList<>(sources.size());
        rotated.addAll(sources.subList(pivot, sources.size()));
        rotated.addAll(sources.subList(0, pivot));
        return rotated;
    }

    private boolean isSourceEnabled(CatalogSource source) {
        ApplicationProperties.SourceConfig cfg = applicationProperties.getScraper().getSources().get(source.sourceCode());
        return cfg == null || cfg.isEnabled();
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
