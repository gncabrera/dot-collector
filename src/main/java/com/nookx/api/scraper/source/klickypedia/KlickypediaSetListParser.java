package com.nookx.api.scraper.source.klickypedia;

import com.nookx.api.scraper.api.PageParser;
import com.nookx.api.scraper.api.ParseContext;
import com.nookx.api.scraper.api.ParseResult;
import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import com.nookx.api.scraper.domain.enumeration.PageType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Parses a Klickypedia theme page (e.g. {@code /themes/freetime/}) into a list of set-detail URLs
 * to enqueue, plus any pagination links.
 * <p>
 * Set numbers in Klickypedia are arbitrary slugs (e.g. {@code 0-gre-playmobil-magazin-45-6-2020}),
 * so we use the slug both as the natural key and as the canonical {@code setNumber}.
 */
@Component
@Profile("scraper")
public class KlickypediaSetListParser implements PageParser {

    private static final Logger LOG = LoggerFactory.getLogger(KlickypediaSetListParser.class);

    /** Matches WordPress' canonical pagination URLs: {@code /themes/<slug>/page/<N>/}. */
    private static final Pattern PAGE_PATH = Pattern.compile("^/themes/([^/]+)/page/(\\d+)/?$");
    /** Matches the leading {@code /themes/<slug>} segment of any theme listing URL. */
    private static final Pattern THEME_PATH = Pattern.compile("^/themes/([^/]+)/?.*$");

    @Override
    public PageType pageType() {
        return PageType.SET_LIST;
    }

    @Override
    public ParseResult parse(ParseContext context) {
        Document doc = Jsoup.parse(context.htmlContent(), KlickypediaConstants.BASE_URL);

        Map<String, String> setSlugToUrl = new LinkedHashMap<>();
        for (Element a : doc.select("a[itemprop=url][href*='" + KlickypediaConstants.SETS_PATH + "']")) {
            String href = a.attr("abs:href");
            String slug = extractSetSlug(href);
            if (slug != null && !setSlugToUrl.containsKey(slug)) {
                setSlugToUrl.put(slug, href);
            }
        }

        Set<String> paginationUrls = extractPagination(doc, context.url());

        LOG.debug("Klickypedia list {} -> {} set refs, {} pagination links", context.url(), setSlugToUrl.size(), paginationUrls.size());

        List<DiscoveredUrl> urls = new ArrayList<>(setSlugToUrl.size() + paginationUrls.size());
        for (Map.Entry<String, String> entry : setSlugToUrl.entrySet()) {
            urls.add(new DiscoveredUrl(entry.getValue(), PageType.SET_DETAIL, entry.getKey()));
        }
        for (String pageUrl : paginationUrls) {
            urls.add(new DiscoveredUrl(pageUrl, PageType.SET_LIST, null));
        }
        return ParseResult.ofUrls(urls);
    }

    /** Extracts the slug from {@code https://www.klickypedia.com/sets/<slug>/} ignoring nested paths. */
    private static String extractSetSlug(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        int idx = url.indexOf(KlickypediaConstants.SETS_PATH);
        if (idx < 0) {
            return null;
        }
        String tail = url.substring(idx + KlickypediaConstants.SETS_PATH.length());
        int slash = tail.indexOf('/');
        if (slash >= 0) {
            tail = tail.substring(0, slash);
        }
        int q = tail.indexOf('?');
        if (q >= 0) {
            tail = tail.substring(0, q);
        }
        tail = tail.trim().toLowerCase(Locale.ROOT);
        return tail.isBlank() ? null : tail;
    }

    /**
     * Returns absolute pagination URLs that point to the same theme listing.
     * <p>
     * Klickypedia's footer pagination is rendered as WordPress' canonical {@code <a class="page-numbers">}
     * widget. Any other anchor that contains {@code /page/} (language switchers,
     * {@code ?elang=}/{@code ?posts_per_page=}/{@code ?display=} variants, etc.) is intentionally
     * dropped: every page is reachable from the canonical pagination so we keep the queue clean.
     * <p>
     * Each accepted URL is normalized to its bare canonical form
     * ({@code https://www.klickypedia.com/themes/<slug>/page/<N>/}) without query string or
     * fragment, and must point to the SAME theme as {@code currentUrl} so a related-themes widget
     * cannot leak in.
     */
    private static Set<String> extractPagination(Document doc, String currentUrl) {
        Set<String> pageUrls = new LinkedHashSet<>();
        String currentTheme = themeSlugOf(currentUrl);
        if (currentTheme == null) {
            return pageUrls;
        }
        String currentCanonical = canonicalize(currentUrl);

        Elements anchors = doc.select("a.page[href]");
        for (Element a : anchors) {
            String href = a.attr("abs:href");
            if (href == null || href.isBlank() || !href.startsWith(KlickypediaConstants.BASE_URL)) {
                continue;
            }
            String canonical = canonicalize(href);
            if (canonical == null) {
                continue;
            }
            String path = canonical.substring(KlickypediaConstants.BASE_URL.length());
            var matcher = PAGE_PATH.matcher(path);
            if (!matcher.matches()) {
                continue;
            }
            if (!currentTheme.equals(matcher.group(1).toLowerCase(Locale.ROOT))) {
                continue;
            }
            if (canonical.equals(currentCanonical)) {
                continue;
            }
            pageUrls.add(canonical);
        }
        return pageUrls;
    }

    /** Strips query string and fragment, then normalizes to lower-case host + original path. */
    private static String canonicalize(String url) {
        if (url == null) {
            return null;
        }
        String trimmed = url.trim();
        int hash = trimmed.indexOf('#');
        if (hash >= 0) {
            trimmed = trimmed.substring(0, hash);
        }
        int q = trimmed.indexOf('?');
        if (q >= 0) {
            trimmed = trimmed.substring(0, q);
        }
        return trimmed.isBlank() ? null : trimmed;
    }

    /** Returns the theme slug from any {@code /themes/<slug>/...} URL, or {@code null}. */
    private static String themeSlugOf(String url) {
        if (url == null || !url.startsWith(KlickypediaConstants.BASE_URL)) {
            return null;
        }
        String path = url.substring(KlickypediaConstants.BASE_URL.length());
        int q = path.indexOf('?');
        if (q >= 0) {
            path = path.substring(0, q);
        }
        var matcher = THEME_PATH.matcher(path);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1).toLowerCase(Locale.ROOT);
    }
}
