package com.nookx.api.scraper.core.store;

import com.nookx.api.scraper.domain.enumeration.PageType;

/**
 * Abstraction over where the raw HTML / binary payloads fetched by the scraper are stored.
 * <p>
 * The default implementation writes gzipped files to local filesystem; a future S3/GCS backend can
 * replace it without touching runners or parsers.
 */
public interface RawContentStore {
    /**
     * Persists a payload and returns a backend-specific path (relative to the base directory,
     * or an object key) that can be stored in {@code scrape_page.storage_path}.
     *
     * @param sourceCode     source code of the page (e.g. "playmodb")
     * @param pageType       role of the page; used for sub-directory layout
     * @param naturalKey     optional natural id; used as filename stem when present
     * @param fallbackKey    filename stem when {@code naturalKey} is null (e.g. a hash of the URL)
     * @param bytes          payload
     * @return a path (or key) identifying the stored object
     */
    String store(String sourceCode, PageType pageType, String naturalKey, String fallbackKey, byte[] bytes);

    /**
     * Reads a previously stored payload. Returns {@code null} if the path no longer exists.
     */
    byte[] read(String storagePath);
}
