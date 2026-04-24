package com.nookx.api.scraper.source.playmodb;

import com.nookx.api.scraper.api.PageParser;
import com.nookx.api.scraper.api.ParseContext;
import com.nookx.api.scraper.api.ParseResult;
import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import com.nookx.api.scraper.domain.enumeration.PageType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
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
 * Parses a PlaymoDB {@code showsetlist.pl?theme=...} page into a list of set-detail URLs to enqueue.
 * <p>
 * Every link that points to {@code showset.pl?num=XXXX} is treated as a set reference. Duplicates
 * are collapsed by the caller because {@code ScrapePageService.enqueueIfAbsent} is idempotent.
 * Pagination links ("next page") are also re-emitted as SET_LIST URLs.
 */
@Component
@Profile("scraper")
public class PlaymoDbSetListParser implements PageParser {

    private static final Logger LOG = LoggerFactory.getLogger(PlaymoDbSetListParser.class);

    private static final Pattern SHOW_SET_NUM = Pattern.compile("showinv\\.pl\\?set=([^&\"']+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHOW_SET_LIST_HREF = Pattern.compile("\\/cgi-bin\\/sets\\.pl\\?[^\"']*", Pattern.CASE_INSENSITIVE);

    @Override
    public PageType pageType() {
        return PageType.SET_LIST;
    }

    @Override
    public ParseResult parse(ParseContext context) {
        Document doc = Jsoup.parse(context.htmlContent(), PlaymoDbConstants.BASE_URL);
        Elements links = doc.select("a[href]");

        Set<String> setNumbers = new LinkedHashSet<>();
        Set<String> pageUrls = new LinkedHashSet<>();
        for (Element a : links) {
            String href = a.attr("abs:href");
            if (href == null || href.isBlank()) {
                continue;
            }
            Matcher numMatcher = SHOW_SET_NUM.matcher(href);
            if (numMatcher.find()) {
                setNumbers.add(numMatcher.group(1));
                continue;
            }
            Matcher listMatcher = SHOW_SET_LIST_HREF.matcher(href);
            if (listMatcher.find() && !href.equals(context.url())) {
                pageUrls.add(href);
            }
        }

        LOG.debug("PlaymoDB list {} -> {} set refs, {} pagination links", context.url(), setNumbers.size(), pageUrls.size());

        List<DiscoveredUrl> urls = new ArrayList<>(setNumbers.size() + pageUrls.size());
        for (String num : setNumbers) {
            // urls.add(new DiscoveredUrl(PlaymoDbConstants.SHOW_SET_URL_PREFIX + num, PageType.SET_DETAIL, num));
        }
        for (String pageUrl : pageUrls) {
            urls.add(new DiscoveredUrl(pageUrl, PageType.SET_LIST, null));
        }
        return ParseResult.ofUrls(urls);
    }
}
