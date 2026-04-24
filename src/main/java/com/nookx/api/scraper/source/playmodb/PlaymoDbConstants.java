package com.nookx.api.scraper.source.playmodb;

/**
 * Shared constants for the PlaymoDB source plugin.
 * <p>
 * Every URL is built relative to {@link #BASE_URL} which must match
 * {@code application.scraper.sources.playmodb.base-url}.
 */
final class PlaymoDbConstants {

    static final String SOURCE_CODE = "playmodb";
    /** Catalog interest every PlaymoDB set belongs to (matches the seeded {@code interest.csv}). */
    static final String INTEREST_NAME = "Playmobil";
    static final String BASE_URL = "https://playmodb.org";
    static final String ALL_THEMES_URL = BASE_URL + "/cgi-bin/sets.pl";
    static final String SETS_BY_THEME_PREFIX_URL = BASE_URL + "/cgi-bin/sets.pl?sortby=name&focus=all&theme=";

    static final String SHOW_SET_LIST_URL = BASE_URL + "/cgi-bin/showinv.pl";
    static final String SHOW_SET_URL_PREFIX = BASE_URL + "/cgi-bin/showinv.pl?set=";
    static final String SHOW_SET_TEXT_ONLY = BASE_URL + "/cgi-bin/showinv.pl?set=";

    private PlaymoDbConstants() {}
}
