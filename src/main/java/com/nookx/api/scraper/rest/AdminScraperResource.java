package com.nookx.api.scraper.rest;

import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import com.nookx.api.scraper.core.runner.AssetFetchRunner;
import com.nookx.api.scraper.core.runner.DiscoveryRunner;
import com.nookx.api.scraper.core.runner.FetchRunner;
import com.nookx.api.scraper.core.runner.ParseRunner;
import com.nookx.api.scraper.core.service.ScrapePageService;
import com.nookx.api.scraper.domain.ScrapePage;
import com.nookx.api.scraper.domain.enumeration.PageType;
import com.nookx.api.scraper.repository.ScrapePageRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin-only endpoints to operate the scraper: seed ad-hoc URLs and tick each runner on demand.
 * <p>
 * All endpoints sit under {@code /api/admin/**}, which {@link com.nookx.api.config.SecurityConfiguration}
 * already restricts to {@code ROLE_ADMIN}. The explicit {@code @PreAuthorize} annotation is kept as a
 * second line of defense and so that the requirement is visible next to the handler.
 */
@RestController
@RequestMapping("/api/admin/scraper")
@Profile("scraper")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminScraperResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdminScraperResource.class);

    private final ScrapePageService scrapePageService;
    private final ScrapePageRepository scrapePageRepository;
    private final DiscoveryRunner discoveryRunner;
    private final FetchRunner fetchRunner;
    private final ParseRunner parseRunner;
    private final AssetFetchRunner assetFetchRunner;

    public AdminScraperResource(
        ScrapePageService scrapePageService,
        ScrapePageRepository scrapePageRepository,
        DiscoveryRunner discoveryRunner,
        FetchRunner fetchRunner,
        ParseRunner parseRunner,
        AssetFetchRunner assetFetchRunner
    ) {
        this.scrapePageService = scrapePageService;
        this.scrapePageRepository = scrapePageRepository;
        this.discoveryRunner = discoveryRunner;
        this.fetchRunner = fetchRunner;
        this.parseRunner = parseRunner;
        this.assetFetchRunner = assetFetchRunner;
    }

    /**
     * Enqueues a single URL into the scrape queue. Useful to seed an end-to-end test with one set.
     */
    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> seed(@Valid @RequestBody SeedUrlRequest request) {
        DiscoveredUrl url = new DiscoveredUrl(request.url(), request.pageType(), request.naturalKey());
        boolean inserted = scrapePageService.enqueueIfAbsent(request.sourceCode(), url);
        LOG.info("Admin seed: source={} pageType={} url={} inserted={}", request.sourceCode(), request.pageType(), request.url(), inserted);
        return ResponseEntity.ok(Map.of("inserted", inserted, "url", request.url()));
    }

    @PostMapping("/trigger/discovery")
    public ResponseEntity<Map<String, Object>> triggerDiscovery() {
        int newUrls = discoveryRunner.run();
        return ResponseEntity.ok(Map.of("newUrls", newUrls));
    }

    @PostMapping("/trigger/fetch")
    public ResponseEntity<Map<String, Object>> triggerFetch() {
        boolean didWork = fetchRunner.runOnce();
        return ResponseEntity.ok(Map.of("didWork", didWork));
    }

    @PostMapping("/trigger/parse")
    public ResponseEntity<Map<String, Object>> triggerParse() {
        int parsed = parseRunner.runOnce();
        return ResponseEntity.ok(Map.of("parsed", parsed));
    }

    @PostMapping("/trigger/asset-fetch")
    public ResponseEntity<Map<String, Object>> triggerAssetFetch() {
        boolean didWork = assetFetchRunner.runOnce();
        return ResponseEntity.ok(Map.of("didWork", didWork));
    }

    @GetMapping("/pages/{id}")
    public ResponseEntity<ScrapePage> getPage(@PathVariable("id") Long id) {
        Optional<ScrapePage> page = scrapePageRepository.findById(id);
        return page.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        long total = scrapePageRepository.count();
        return ResponseEntity.ok(Map.of("totalPages", total, "now", Instant.now()));
    }

    /**
     * Body of {@link #seed(SeedUrlRequest)}.
     */
    public record SeedUrlRequest(@NotBlank String sourceCode, @NotBlank String url, @NotNull PageType pageType, String naturalKey) {}
}
