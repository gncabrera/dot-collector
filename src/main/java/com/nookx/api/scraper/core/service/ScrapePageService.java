package com.nookx.api.scraper.core.service;

import com.nookx.api.config.ApplicationProperties;
import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import com.nookx.api.scraper.core.FetchResult;
import com.nookx.api.scraper.domain.ScrapePage;
import com.nookx.api.scraper.domain.enumeration.FetchStatus;
import com.nookx.api.scraper.domain.enumeration.ParseStatus;
import com.nookx.api.scraper.repository.ScrapePageRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages the {@code scrape_page} work queue.
 * <p>
 * Every state transition lives here so runners stay small and free of persistence logic.
 * Recheck scheduling follows the policy described in {@link ApplicationProperties.Recheck} and
 * agreed with the product side:
 * <ul>
 *     <li>Fresh set (release < {@code recentSetAgeYears}): recheck in {@code recentSetReleaseDays} days.</li>
 *     <li>Everything else: recheck in {@code defaultDays} days.</li>
 *     <li>{@code 404}: recheck in {@code notFoundDays} days (source may resurrect the page).</li>
 *     <li>Transient (5xx / timeout): retry in {@code transientRetryMinutes}, up to {@code maxRetries}.</li>
 *     <li>Unchanged (same content hash) extends the current interval by x1.5 (soft back-off).</li>
 * </ul>
 */
@Service
@Profile("scraper")
@Transactional
public class ScrapePageService {

    private static final Logger LOG = LoggerFactory.getLogger(ScrapePageService.class);
    private static final int MAX_SOFT_BACKOFF_DAYS = 180;

    private final ScrapePageRepository scrapePageRepository;
    private final ApplicationProperties.Recheck recheck;

    public ScrapePageService(ScrapePageRepository scrapePageRepository, ApplicationProperties applicationProperties) {
        this.scrapePageRepository = scrapePageRepository;
        this.recheck = applicationProperties.getScraper().getRecheck();
    }

    /**
     * Creates a new {@link ScrapePage} row for the given URL if one does not already exist.
     *
     * @return {@code true} if a new row was inserted, {@code false} if the URL was already tracked
     */
    public boolean enqueueIfAbsent(String sourceCode, DiscoveredUrl url) {
        Optional<ScrapePage> existing = scrapePageRepository.findBySourceCodeAndUrl(sourceCode, url.url());
        if (existing.isPresent()) {
            return false;
        }
        ScrapePage page = new ScrapePage();
        page.setSourceCode(sourceCode);
        page.setPageType(url.pageType());
        page.setUrl(url.url());
        page.setNaturalKey(url.naturalKey());
        page.setFetchStatus(FetchStatus.PENDING);
        page.setParseStatus(ParseStatus.PENDING);
        Instant now = Instant.now();
        page.setDiscoveredAt(now);
        page.setNextCheckAt(now);
        scrapePageRepository.save(page);
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<ScrapePage> pickNextDue(String sourceCode) {
        List<ScrapePage> due = scrapePageRepository.findDueForFetch(
            sourceCode,
            FetchStatus.PENDING,
            FetchStatus.NOT_MODIFIED,
            FetchStatus.DONE,
            FetchStatus.TRANSIENT_ERROR,
            FetchStatus.NOT_FOUND,
            Instant.now(),
            PageRequest.of(0, 1)
        );
        return due.stream().findFirst();
    }

    @Transactional(readOnly = true)
    public List<ScrapePage> findPendingParse(int limit) {
        return scrapePageRepository.findByParseStatusOrderByFetchedAtAsc(ParseStatus.PENDING, PageRequest.of(0, limit));
    }

    public void markFetchSuccess(ScrapePage page, FetchResult result, String storagePath, String contentHash, LocalDate releaseDate) {
        Instant now = Instant.now();
        boolean changed = contentHash == null || !contentHash.equals(page.getContentHash());
        page.setHttpStatus(result.status());
        page.setEtag(result.etag());
        page.setLastModified(result.lastModified());
        page.setContentHash(contentHash);
        page.setContentSizeBytes(result.body() == null ? null : (long) result.body().length);
        page.setStoragePath(storagePath);
        page.setFetchStatus(FetchStatus.DONE);
        page.setFetchedAt(now);
        page.setRetryCount(0);
        page.setLastError(null);
        if (changed) {
            page.setParseStatus(ParseStatus.PENDING);
        }
        page.setNextCheckAt(now.plus(resolveDefaultInterval(releaseDate), ChronoUnit.DAYS));
        scrapePageRepository.save(page);
    }

    public void markFetchNotModified(ScrapePage page, LocalDate releaseDate) {
        Instant now = Instant.now();
        page.setHttpStatus(304);
        page.setFetchStatus(FetchStatus.NOT_MODIFIED);
        page.setFetchedAt(now);
        page.setRetryCount(0);
        page.setLastError(null);
        long base = resolveDefaultInterval(releaseDate);
        long backedOff = Math.min((long) Math.ceil(base * 1.5), MAX_SOFT_BACKOFF_DAYS);
        page.setNextCheckAt(now.plus(backedOff, ChronoUnit.DAYS));
        scrapePageRepository.save(page);
    }

    public void markFetchNotFound(ScrapePage page) {
        Instant now = Instant.now();
        page.setHttpStatus(404);
        page.setFetchStatus(FetchStatus.NOT_FOUND);
        page.setFetchedAt(now);
        page.setLastError(null);
        page.setNextCheckAt(now.plus(recheck.getNotFoundDays(), ChronoUnit.DAYS));
        scrapePageRepository.save(page);
    }

    public void markFetchTransient(ScrapePage page, String error) {
        Instant now = Instant.now();
        page.setFetchStatus(FetchStatus.TRANSIENT_ERROR);
        page.setFetchedAt(now);
        page.setRetryCount(page.getRetryCount() + 1);
        page.setLastError(truncate(error));
        if (page.getRetryCount() >= recheck.getMaxRetries()) {
            page.setFetchStatus(FetchStatus.DEAD);
            page.setNextCheckAt(now.plus(recheck.getNotFoundDays(), ChronoUnit.DAYS));
        } else {
            page.setNextCheckAt(now.plus(recheck.getTransientRetryMinutes(), ChronoUnit.MINUTES));
        }
        scrapePageRepository.save(page);
    }

    public void markParseSuccess(ScrapePage page) {
        page.setParseStatus(ParseStatus.DONE);
        page.setParsedAt(Instant.now());
        page.setLastError(null);
        scrapePageRepository.save(page);
    }

    public void markParseFailure(ScrapePage page, String error) {
        page.setParseStatus(ParseStatus.FAILED);
        page.setParsedAt(Instant.now());
        page.setLastError(truncate(error));
        scrapePageRepository.save(page);
    }

    private long resolveDefaultInterval(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isAfter(LocalDate.now().minus(Period.ofYears(recheck.getRecentSetAgeYears())))) {
            return recheck.getRecentSetReleaseDays();
        }
        return recheck.getDefaultDays();
    }

    private static String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() > 1990 ? value.substring(0, 1990) + "..." : value;
    }
}
