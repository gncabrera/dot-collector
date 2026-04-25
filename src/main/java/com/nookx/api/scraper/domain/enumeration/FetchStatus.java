package com.nookx.api.scraper.domain.enumeration;

/**
 * Lifecycle of the fetch phase for a {@code scrape_page} row.
 */
public enum FetchStatus {
    /** Never fetched successfully (new row or permanently failed). */
    PENDING,
    /** Currently being fetched by a runner. Not used for a single-threaded runner but reserved for future. */
    IN_PROGRESS,
    /** Last fetch returned HTTP 2xx. HTML stored. */
    DONE,
    /** Last fetch returned HTTP 304 Not Modified. Stored content is still valid. */
    NOT_MODIFIED,
    /** Last fetch returned a transient error (5xx, timeout). Will be retried. */
    TRANSIENT_ERROR,
    /** Last fetch returned HTTP 404. Treated as permanent but will be re-checked periodically. */
    NOT_FOUND,
    /** Exceeded max retries. Will be re-checked only manually or after a long cooldown. */
    DEAD,
}
