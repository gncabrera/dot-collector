package com.nookx.api.scraper.api;

import com.nookx.api.scraper.domain.enumeration.PageType;

/**
 * Input for a {@link PageParser}. Decoupled from JPA / the scraper pipeline so parsers can be
 * unit-tested with a plain HTML fixture.
 *
 * @param sourceCode  code of the source that produced this page (e.g. "playmodb")
 * @param pageType    role of the page in the source pipeline
 * @param url         absolute URL the HTML was fetched from (useful to resolve relative links)
 * @param naturalKey  source-specific natural id if known (setNumber, themeName, ...)
 * @param htmlContent decoded HTML body
 */
public record ParseContext(String sourceCode, PageType pageType, String url, String naturalKey, String htmlContent) {}
