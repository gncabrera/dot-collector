package com.nookx.api.scraper.core.runner;

import com.nookx.api.config.ApplicationProperties;
import com.nookx.api.scraper.api.CatalogSource;
import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import com.nookx.api.scraper.core.service.ScrapePageService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically asks every enabled {@link CatalogSource} for fresh URLs and enqueues whatever is new.
 * <p>
 * Runs once every {@code application.scraper.discovery-delay-ms} (default 7 days) and also on
 * demand via the admin endpoint. Discovery itself is allowed to do a small burst of HTTP requests
 * because it happens rarely and only emits metadata pages, not detail pages.
 */
@Component
@Profile("scraper")
public class DiscoveryRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryRunner.class);

    private final List<CatalogSource> sources;
    private final ScrapePageService scrapePageService;
    private final ApplicationProperties applicationProperties;

    public DiscoveryRunner(List<CatalogSource> sources, ScrapePageService scrapePageService, ApplicationProperties applicationProperties) {
        this.sources = sources;
        this.scrapePageService = scrapePageService;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Scheduled trigger. Uses {@code initialDelayString} / {@code fixedDelayString} so the value
     * is resolved from the {@link ApplicationProperties} instance registered as a bean.
     */
    @Scheduled(
        fixedDelayString = "${application.scraper.discovery-delay-ms:604800000}",
        initialDelayString = "${application.scraper.discovery-initial-delay-ms:604800000}"
    )
    public void tick() {
        run();
    }

    public int run() {
        int totalNew = 0;
        for (CatalogSource source : sources) {
            if (!isSourceEnabled(source)) {
                continue;
            }
            int newForSource = 0;
            try {
                List<DiscoveredUrl> urls = source.discoverer().discover();
                for (DiscoveredUrl url : urls) {
                    if (scrapePageService.enqueueIfAbsent(source.sourceCode(), url)) {
                        newForSource++;
                    }
                }
                LOG.info("Discovery for {}: {} URLs reported, {} new in queue", source.sourceCode(), urls.size(), newForSource);
                totalNew += newForSource;
            } catch (Exception e) {
                LOG.error("Discovery failed for source {}", source.sourceCode(), e);
            }
        }
        return totalNew;
    }

    private boolean isSourceEnabled(CatalogSource source) {
        ApplicationProperties.SourceConfig cfg = applicationProperties.getScraper().getSources().get(source.sourceCode());
        return cfg == null || cfg.isEnabled();
    }
}
