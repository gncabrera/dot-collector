package com.nookx.api.scraper.api;

import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import java.util.List;

/**
 * Produces the initial list of URLs to crawl for a source.
 * <p>
 * Typical implementations fetch a landing page / sitemap / theme index and emit one
 * {@link DiscoveredUrl} per entity detail page. Discovery runs infrequently (days) so
 * the implementation is allowed to do a handful of HTTP calls of its own through
 * {@link com.nookx.api.scraper.core.ScraperHttpClient}.
 */
public interface SourceDiscoverer {
    List<DiscoveredUrl> discover();
}
