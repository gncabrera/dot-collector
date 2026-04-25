package com.nookx.api.scraper.source.playmodb;

import com.nookx.api.scraper.api.SourceDiscoverer;
import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import com.nookx.api.scraper.core.FetchResult;
import com.nookx.api.scraper.core.ScraperHttpClient;
import com.nookx.api.scraper.domain.enumeration.PageType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
 * Discovers PlaymoDB URLs by enumerating every theme from the theme form and submitting the
 * equivalent of "select every checkbox + click List sets".
 * <p>
 * Strategy:
 * <ol>
 *     <li>GET {@code /cgi-bin/sets.pl}, extract the list of {@code theme=*} values exposed as checkboxes.</li>
 *     <li>For each theme, fetch the showsetlist.pl page and enqueue it as a {@link PageType#SET_LIST}.</li>
 * </ol>
 * <p>
 * The actual set detail URLs (SET_DETAIL) are not enumerated here on purpose: the SET_LIST parser
 * emits them as new discovered URLs from inside the pipeline. That keeps discovery cheap (~1
 * request per theme) and lets pagination be driven by the parser.
 */
@Component
@Profile("scraper")
public class PlaymoDbDiscoverer implements SourceDiscoverer {

    private static final Logger LOG = LoggerFactory.getLogger(PlaymoDbDiscoverer.class);

    private final ScraperHttpClient httpClient;

    public PlaymoDbDiscoverer(ScraperHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public List<DiscoveredUrl> discover() {
        FetchResult themeForm = httpClient.get(PlaymoDbConstants.ALL_THEMES_URL);
        if (!themeForm.isOk()) {
            LOG.warn("PlaymoDB discovery could not load theme form (status={})", themeForm.status());
            return List.of();
        }
        Set<String> themes = extractThemes(new String(themeForm.body(), StandardCharsets.UTF_8));
        LOG.info("PlaymoDB discovery found {} themes", themes.size());

        List<DiscoveredUrl> urls = new ArrayList<>(themes.size());
        for (String theme : themes) {
            String encoded = URLEncoder.encode(theme, StandardCharsets.UTF_8);
            String url = PlaymoDbConstants.SETS_BY_THEME_PREFIX_URL + encoded;
            urls.add(new DiscoveredUrl(url, PageType.SET_LIST, theme));
        }
        return urls;
    }

    private static Set<String> extractThemes(String html) {
        Document doc = Jsoup.parse(html);
        Elements boxes = doc.select("input[type=checkbox][name=theme]");
        Set<String> themes = new LinkedHashSet<>();
        for (Element box : boxes) {
            String value = box.attr("value");
            if (value != null && !value.isBlank()) {
                themes.add(value);
            }
        }
        return themes;
    }
}
