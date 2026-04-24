package com.nookx.api.scraper.source.klickypedia;

import com.nookx.api.scraper.api.SourceDiscoverer;
import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import com.nookx.api.scraper.core.FetchResult;
import com.nookx.api.scraper.core.ScraperHttpClient;
import com.nookx.api.scraper.domain.enumeration.PageType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Discovers Klickypedia URLs by enumerating every theme page from {@code /sets/}.
 * <p>
 * Strategy:
 * <ol>
 *     <li>GET {@code /sets/}, extract every {@code <a itemprop="url" href=".../themes/<slug>/">} link.</li>
 *     <li>Enqueue each theme URL as a {@link PageType#SET_LIST}; the natural key is the theme slug.</li>
 * </ol>
 * <p>
 * Individual set detail URLs are not enumerated here on purpose: the SET_LIST parser emits them
 * incrementally, which keeps discovery cheap (one request) and lets pagination be driven from the
 * parser side.
 */
@Component
@Profile("scraper")
public class KlickypediaDiscoverer implements SourceDiscoverer {

    private static final Logger LOG = LoggerFactory.getLogger(KlickypediaDiscoverer.class);

    private final ScraperHttpClient httpClient;

    public KlickypediaDiscoverer(ScraperHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public List<DiscoveredUrl> discover() {
        FetchResult result = httpClient.get(KlickypediaConstants.ALL_SETS_URL);
        if (!result.isOk()) {
            LOG.warn("Klickypedia discovery could not load {} (status={})", KlickypediaConstants.ALL_SETS_URL, result.status());
            return List.of();
        }
        Set<ThemeRef> themes = extractThemes(new String(result.body(), StandardCharsets.UTF_8));
        LOG.info("Klickypedia discovery found {} themes", themes.size());

        List<DiscoveredUrl> urls = new ArrayList<>(themes.size());
        for (ThemeRef theme : themes) {
            urls.add(new DiscoveredUrl(theme.url(), PageType.SET_LIST, theme.slug()));
        }
        return urls;
    }

    private static Set<ThemeRef> extractThemes(String html) {
        Document doc = Jsoup.parse(html, KlickypediaConstants.BASE_URL);
        Elements links = doc.select("a[itemprop=url][href*='" + KlickypediaConstants.THEMES_PATH + "']");
        Set<ThemeRef> themes = new LinkedHashSet<>();
        for (Element a : links) {
            String href = a.attr("abs:href");
            if (href == null || href.isBlank()) {
                continue;
            }
            String slug = extractSlug(href);
            if (slug == null) {
                continue;
            }
            themes.add(new ThemeRef(href, slug));
        }
        return themes;
    }

    /** Extracts the theme slug from {@code https://www.klickypedia.com/themes/<slug>/}. */
    private static String extractSlug(String url) {
        int idx = url.indexOf(KlickypediaConstants.THEMES_PATH);
        if (idx < 0) {
            return null;
        }
        String tail = url.substring(idx + KlickypediaConstants.THEMES_PATH.length());
        // Strip trailing slash and any query/fragment.
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

    private record ThemeRef(String url, String slug) {}
}
