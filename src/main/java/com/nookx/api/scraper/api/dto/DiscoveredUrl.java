package com.nookx.api.scraper.api.dto;

import com.nookx.api.scraper.domain.enumeration.PageType;

/**
 * URL produced by a {@link com.nookx.api.scraper.api.SourceDiscoverer}, ready to be inserted
 * into the {@code scrape_page} queue.
 *
 * @param url        absolute URL to fetch
 * @param pageType   role of this URL in the source pipeline
 * @param naturalKey optional source-specific natural id (setNumber, themeName, ...)
 */
public record DiscoveredUrl(String url, PageType pageType, String naturalKey) {}
