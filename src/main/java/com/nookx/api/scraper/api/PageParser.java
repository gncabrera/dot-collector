package com.nookx.api.scraper.api;

import com.nookx.api.scraper.domain.enumeration.PageType;

/**
 * Source-specific parser for a single {@link PageType}.
 * <p>
 * Implementations MUST be pure w.r.t. JPA/IO: given the same {@link ParseContext} they must always
 * return the same {@link ParseResult}. That makes re-parsing a previously stored HTML snapshot safe
 * and unit-testing trivial.
 */
public interface PageParser {
    PageType pageType();

    ParseResult parse(ParseContext context);
}
