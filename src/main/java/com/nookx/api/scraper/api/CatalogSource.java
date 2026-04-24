package com.nookx.api.scraper.api;

import com.nookx.api.scraper.domain.enumeration.PageType;
import java.util.Optional;

/**
 * Entry point of a source plugin (e.g. PlaymoDB, Rebrickable, Hot Wheels, ...).
 * <p>
 * Each implementation is registered as a Spring bean guarded by a {@code @ConditionalOnProperty}
 * on its specific {@code application.scraper.sources.<code>.enabled} flag. The engine injects
 * {@code List<CatalogSource>} and iterates over whatever is enabled.
 */
public interface CatalogSource {
    /** Stable code used in DB ({@code scrape_page.source_code}, ...) and config keys. */
    String sourceCode();

    /** Discoverer used by the discovery runner to populate the queue. */
    SourceDiscoverer discoverer();

    /** Returns the parser for a given page type, if this source handles it. */
    Optional<PageParser> parserFor(PageType pageType);
}
