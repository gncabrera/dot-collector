package com.nookx.api.scraper.source.klickypedia;

/**
 * Shared constants for the Klickypedia source plugin.
 * <p>
 * Every URL is built relative to {@link #BASE_URL} which must match
 * {@code application.scraper.sources.klickypedia.base-url}.
 */
final class KlickypediaConstants {

    static final String SOURCE_CODE = "klickypedia";
    static final String BASE_URL = "https://www.klickypedia.com";
    /** Discovery entry point: lists every theme link. */
    static final String ALL_SETS_URL = BASE_URL + "/sets/";
    /** Marker used inside theme URLs (e.g. {@code /themes/sports/}). */
    static final String THEMES_PATH = "/themes/";
    /** Marker used inside individual set URLs (e.g. {@code /sets/72057-soccer-training-center/}). */
    static final String SETS_PATH = "/sets/";

    private KlickypediaConstants() {}
}
