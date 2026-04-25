package com.nookx.api.scraper.core.runner;

import com.nookx.api.scraper.api.CatalogSource;
import com.nookx.api.scraper.api.PageParser;
import com.nookx.api.scraper.api.ParseContext;
import com.nookx.api.scraper.api.ParseResult;
import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import com.nookx.api.scraper.api.dto.NormalizedSetDto;
import com.nookx.api.scraper.core.service.ScrapePageService;
import com.nookx.api.scraper.core.service.SourceIngestService;
import com.nookx.api.scraper.core.store.RawContentStore;
import com.nookx.api.scraper.domain.ScrapePage;
import com.nookx.api.scraper.domain.enumeration.FetchStatus;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Drains the parse queue: pages with {@code parse_status=PENDING} that have been successfully
 * fetched at least once.
 * <p>
 * Parsing is a local, CPU-bound step that does not hit the network, so we process up to
 * {@value #BATCH_SIZE} pages per tick (by default every 10 seconds).
 */
@Component
@Profile("scraper")
public class ParseRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ParseRunner.class);
    private static final int BATCH_SIZE = 20;

    private final List<CatalogSource> sources;
    private final ScrapePageService scrapePageService;
    private final SourceIngestService ingestService;
    private final RawContentStore rawContentStore;
    private final Map<String, CatalogSource> sourcesByCode;

    public ParseRunner(
        List<CatalogSource> sources,
        ScrapePageService scrapePageService,
        SourceIngestService ingestService,
        RawContentStore rawContentStore
    ) {
        this.sources = sources;
        this.scrapePageService = scrapePageService;
        this.ingestService = ingestService;
        this.rawContentStore = rawContentStore;
        this.sourcesByCode = new HashMap<>();
        for (CatalogSource source : sources) {
            sourcesByCode.put(source.sourceCode(), source);
        }
    }

    @Scheduled(
        fixedDelayString = "${application.scraper.parse-delay-ms:10000}",
        initialDelayString = "${application.scraper.parse-initial-delay-ms:10000}"
    )
    public void tick() {
        runOnce();
    }

    public int runOnce() {
        List<ScrapePage> batch = scrapePageService.findPendingParse(BATCH_SIZE);
        if (batch.isEmpty()) {
            return 0;
        }
        int processed = 0;
        for (ScrapePage page : batch) {
            if (page.getFetchStatus() != FetchStatus.DONE && page.getFetchStatus() != FetchStatus.NOT_MODIFIED) {
                continue;
            }
            try {
                parseOne(page);
                processed++;
            } catch (Exception e) {
                LOG.error("Parse failed for {} [{}]", page.getUrl(), page.getId(), e);
                scrapePageService.markParseFailure(page, e.toString());
            }
        }
        LOG.debug("ParseRunner tick: processed {} pages", processed);
        return processed;
    }

    private void parseOne(ScrapePage page) {
        CatalogSource source = sourcesByCode.get(page.getSourceCode());
        if (source == null) {
            LOG.warn("No source registered for code {}, skipping parse of {}", page.getSourceCode(), page.getUrl());
            scrapePageService.markParseFailure(page, "No source registered for code " + page.getSourceCode());
            return;
        }
        Optional<PageParser> parser = source.parserFor(page.getPageType());
        if (parser.isEmpty()) {
            LOG.debug("Source {} has no parser for {}, marking as not-applicable", source.sourceCode(), page.getPageType());
            scrapePageService.markParseSuccess(page);
            return;
        }
        byte[] raw = rawContentStore.read(page.getStoragePath());
        if (raw == null) {
            throw new IllegalStateException("Raw content missing at " + page.getStoragePath());
        }
        String html = new String(raw, StandardCharsets.UTF_8);
        ParseContext ctx = new ParseContext(page.getSourceCode(), page.getPageType(), page.getUrl(), page.getNaturalKey(), html);

        ParseResult result = parser.get().parse(ctx);

        for (NormalizedSetDto set : result.sets()) {
            ingestService.ingest(set, page);
        }
        for (DiscoveredUrl newUrl : result.newUrls()) {
            scrapePageService.enqueueIfAbsent(page.getSourceCode(), newUrl);
        }
        scrapePageService.markParseSuccess(page);
    }
}
