package com.nookx.api.scraper.domain.enumeration;

/**
 * Type of page tracked by the scraper queue.
 * <p>
 * Each source plugin registers a {@code PageParser} per relevant type. Adding new
 * types is safe: a plugin that does not know a given type simply does not register
 * a parser for it.
 */
public enum PageType {
    /** Top-level discovery page (e.g. list of themes, sitemap index). */
    DISCOVERY_INDEX,
    /** Paginated listing of sets (by theme, year, series, ...). */
    SET_LIST,
    /** Detail page of a single set. */
    SET_DETAIL,
    /** Detail page of a part / component. */
    PART_DETAIL,
    /** Binary image referenced from a detail page. */
    IMAGE,
    /** Binary instructions PDF. */
    PDF,
}
