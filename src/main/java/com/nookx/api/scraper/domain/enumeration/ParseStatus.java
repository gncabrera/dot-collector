package com.nookx.api.scraper.domain.enumeration;

/**
 * Lifecycle of the parse phase for a {@code scrape_page} row.
 * <p>
 * A page becomes parseable once it has been fetched successfully at least once.
 */
public enum ParseStatus {
    /** Not parsed yet (or the stored content changed and needs reparse). */
    PENDING,
    /** Parsed successfully. Staging rows written. */
    DONE,
    /** Parser threw an exception. Manual inspection needed. */
    FAILED,
    /** Nothing to parse for this page (e.g. binary asset page). */
    NOT_APPLICABLE,
}
