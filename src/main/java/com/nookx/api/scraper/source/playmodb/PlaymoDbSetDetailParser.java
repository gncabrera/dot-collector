package com.nookx.api.scraper.source.playmodb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nookx.api.scraper.api.PageParser;
import com.nookx.api.scraper.api.ParseContext;
import com.nookx.api.scraper.api.ParseResult;
import com.nookx.api.scraper.api.dto.NormalizedAssetDto;
import com.nookx.api.scraper.api.dto.NormalizedSetDto;
import com.nookx.api.scraper.domain.enumeration.PageType;
import com.nookx.api.scraper.domain.enumeration.SourceAssetKind;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
 * Parses a PlaymoDB {@code showset.pl?num=XXXX} page into a {@link NormalizedSetDto}.
 * <p>
 * PlaymoDB renders data in loosely-structured HTML with label/value pairs. Rather than hard-coding
 * selectors that will break the first time the site is tweaked, the parser:
 * <ul>
 *     <li>Uses the URL's {@code num} parameter as the authoritative {@code sourceExternalId}.</li>
 *     <li>Extracts a best-effort name from the first {@code <h1>} / {@code <h2>} / {@code <title>}.</li>
 *     <li>Scans {@code <tr>} and {@code <dt>/<dd>} pairs, putting every label/value into
 *         {@code rawAttributes}. Known keys (theme, release year, description) are also promoted to
 *         typed fields.</li>
 *     <li>Collects every {@code <img>} whose absolute URL is hosted under playmodb.org and treats
 *         any PDF link as instructions.</li>
 * </ul>
 */
@Component
@Profile("scraper")
public class PlaymoDbSetDetailParser implements PageParser {

    private static final Logger LOG = LoggerFactory.getLogger(PlaymoDbSetDetailParser.class);

    private static final Pattern NUM_PARAM = Pattern.compile("[?&]num=([^&]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\b(19\\d{2}|20\\d{2})\\b");
    private static final Pattern FULL_DATE_PATTERN = Pattern.compile("\\b(\\d{4})-(\\d{2})-(\\d{2})\\b");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ObjectMapper objectMapper;

    public PlaymoDbSetDetailParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public PageType pageType() {
        return PageType.SET_DETAIL;
    }

    @Override
    public ParseResult parse(ParseContext context) {
        Document doc = Jsoup.parse(context.htmlContent(), PlaymoDbConstants.BASE_URL);

        String setNumber = extractSetNumber(context.url(), context.naturalKey());
        if (setNumber == null) {
            LOG.warn("Could not derive setNumber for {}", context.url());
            return ParseResult.empty();
        }

        String name = extractName(doc, setNumber);
        Map<String, String> labelValues = extractLabelValues(doc);
        String theme = firstNonBlank(labelValues.get("theme"), labelValues.get("themes"));
        LocalDate releaseDate = extractReleaseDate(labelValues);
        String description = firstNonBlank(labelValues.get("description"), labelValues.get("notes"), labelValues.get("info"));

        List<NormalizedAssetDto> assets = extractAssets(doc);
        JsonNode rawAttributes = buildRawAttributes(labelValues);

        NormalizedSetDto dto = new NormalizedSetDto(
            PlaymoDbConstants.SOURCE_CODE,
            setNumber,
            setNumber,
            name,
            description,
            releaseDate,
            theme,
            PlaymoDbConstants.INTEREST_NAME,
            rawAttributes,
            assets
        );
        return ParseResult.ofSet(dto);
    }

    private static String extractSetNumber(String url, String naturalKey) {
        if (naturalKey != null && !naturalKey.isBlank()) {
            return naturalKey;
        }
        Matcher matcher = NUM_PARAM.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String extractName(Document doc, String fallback) {
        Element h1 = doc.selectFirst("h1");
        if (h1 != null && !h1.text().isBlank()) {
            return h1.text().trim();
        }
        Element h2 = doc.selectFirst("h2");
        if (h2 != null && !h2.text().isBlank()) {
            return h2.text().trim();
        }
        String title = doc.title();
        if (title != null && !title.isBlank()) {
            return title.trim();
        }
        return "Set#" + fallback;
    }

    private static Map<String, String> extractLabelValues(Document doc) {
        Map<String, String> map = new LinkedHashMap<>();

        Elements rows = doc.select("tr");
        for (Element row : rows) {
            Elements cells = row.select("th, td");
            if (cells.size() >= 2) {
                String key = normalizeKey(cells.get(0).text());
                String value = cells.get(1).text().trim();
                if (!key.isEmpty() && !value.isEmpty() && !map.containsKey(key)) {
                    map.put(key, value);
                }
            }
        }

        Elements dts = doc.select("dt");
        for (Element dt : dts) {
            Element dd = dt.nextElementSibling();
            if (dd != null && "dd".equalsIgnoreCase(dd.tagName())) {
                String key = normalizeKey(dt.text());
                String value = dd.text().trim();
                if (!key.isEmpty() && !value.isEmpty() && !map.containsKey(key)) {
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    private static String normalizeKey(String label) {
        if (label == null) {
            return "";
        }
        String trimmed = label.trim().toLowerCase(Locale.ROOT).replace(':', ' ').trim();
        return trimmed.replaceAll("\\s+", " ");
    }

    private static LocalDate extractReleaseDate(Map<String, String> labelValues) {
        for (String candidate : labelValues.keySet()) {
            if (candidate.contains("release") || candidate.contains("year") || candidate.contains("date")) {
                String value = labelValues.get(candidate);
                LocalDate parsed = parseDate(value);
                if (parsed != null) {
                    return parsed;
                }
            }
        }
        return null;
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        Matcher full = FULL_DATE_PATTERN.matcher(value);
        if (full.find()) {
            try {
                return LocalDate.parse(full.group(0), ISO_DATE);
            } catch (Exception ignore) {
                // fall through
            }
        }
        Matcher year = YEAR_PATTERN.matcher(value);
        if (year.find()) {
            try {
                return LocalDate.of(Integer.parseInt(year.group(1)), 1, 1);
            } catch (NumberFormatException ignore) {
                // fall through
            }
        }
        return null;
    }

    private List<NormalizedAssetDto> extractAssets(Document doc) {
        List<NormalizedAssetDto> assets = new ArrayList<>();
        int imageIndex = 0;

        Elements images = doc.select("img[src]");
        for (Element img : images) {
            String absUrl = img.attr("abs:src");
            if (absUrl.isBlank() || !isSameHost(absUrl)) {
                continue;
            }
            if (isIconish(absUrl)) {
                continue;
            }
            String label = firstNonBlank(img.attr("alt"), img.attr("title"));
            assets.add(new NormalizedAssetDto(absUrl, SourceAssetKind.IMAGE, label, imageIndex++));
        }

        int pdfIndex = 0;
        Elements anchors = doc.select("a[href$=.pdf], a[href$=.PDF]");
        for (Element a : anchors) {
            String absUrl = a.attr("abs:href");
            if (absUrl.isBlank() || !isSameHost(absUrl)) {
                continue;
            }
            String label = firstNonBlank(a.attr("title"), a.text());
            assets.add(new NormalizedAssetDto(absUrl, SourceAssetKind.INSTRUCTIONS_PDF, label, pdfIndex++));
        }

        return assets;
    }

    private JsonNode buildRawAttributes(Map<String, String> labelValues) {
        ObjectNode node = objectMapper.createObjectNode();
        for (Map.Entry<String, String> entry : labelValues.entrySet()) {
            node.put(entry.getKey(), entry.getValue());
        }
        return node;
    }

    private static boolean isSameHost(String absoluteUrl) {
        return absoluteUrl.startsWith(PlaymoDbConstants.BASE_URL + "/") || absoluteUrl.equals(PlaymoDbConstants.BASE_URL);
    }

    private static boolean isIconish(String absoluteUrl) {
        String lower = absoluteUrl.toLowerCase(Locale.ROOT);
        return lower.endsWith(".ico") || lower.contains("/icons/") || lower.contains("valid-") || lower.contains("logo");
    }

    private static String firstNonBlank(String... candidates) {
        for (String s : candidates) {
            if (s != null && !s.isBlank()) {
                return s.trim();
            }
        }
        return null;
    }
}
