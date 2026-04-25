package com.nookx.api.scraper.source.klickypedia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Parses a Klickypedia set detail page (e.g. {@code /sets/72057-soccer-training-center/}) into a
 * {@link NormalizedSetDto}.
 * <p>
 * The set info block is rendered as a flat sequence of children inside
 * {@code div.caja_set_info}. Each block is delimited by {@code <br>} and follows one of two shapes:
 * <ol>
 *     <li>"Localized name": {@code <img src=".../flag-<country>.png">} followed by a text node.</li>
 *     <li>"Labelled value": {@code <strong>Label:</strong>} followed by either an {@code <a>} (used
 *         as the value, e.g. {@code Sports}) or plain text (e.g. {@code none}).</li>
 * </ol>
 * Tags are grabbed from the trailing {@code div.settags > a} block, and images come from the main
 * thumbnail wrapper plus any {@code <a rel="lightbox[set]">} thumbnails.
 * <p>
 * Per the agreed contract, only the English name lands in {@link NormalizedSetDto#name()}; every
 * other field is exposed through {@code rawAttributes} so the front can render them dynamically.
 */
@Component
@Profile("scraper")
public class KlickypediaSetDetailParser implements PageParser {

    private static final Logger LOG = LoggerFactory.getLogger(KlickypediaSetDetailParser.class);

    private static final String FLAG_EN = "flag-greatbritain";
    private static final String FLAG_ES = "flag-spain";
    private static final String FLAG_DE = "flag-germany";
    private static final String FLAG_FR = "flag-france";

    private static final String RELEASED_KEY = "released";
    private static final Pattern FULL_DATE_PATTERN = Pattern.compile("\\b(\\d{4})-(\\d{2})-(\\d{2})\\b");
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\b(19\\d{2}|20\\d{2})\\b");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ObjectMapper objectMapper;

    public KlickypediaSetDetailParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public PageType pageType() {
        return PageType.SET_DETAIL;
    }

    @Override
    public ParseResult parse(ParseContext context) {
        Document doc = Jsoup.parse(context.htmlContent(), KlickypediaConstants.BASE_URL);

        String slug = extractSlug(context.url(), context.naturalKey());
        if (slug == null) {
            LOG.warn("Could not derive set slug for {}", context.url());
            return ParseResult.empty();
        }

        Element infoBlock = doc.selectFirst("div.caja_set_info");
        Map<String, String> names = new LinkedHashMap<>();
        Map<String, String> labelled = new LinkedHashMap<>();
        if (infoBlock != null) {
            extractInfoBlock(infoBlock, names, labelled);
        } else {
            LOG.warn("No div.caja_set_info on {}", context.url());
        }

        List<String> tags = extractTags(doc);
        List<NormalizedAssetDto> assets = extractImages(doc);

        String name = firstNonBlank(names.get("name"), labelled.get("name"));
        LocalDate releaseDate = parseReleaseDate(labelled.get(RELEASED_KEY));
        ObjectNode rawAttributes = buildRawAttributes(names, labelled, tags);

        NormalizedSetDto dto = new NormalizedSetDto(
            KlickypediaConstants.SOURCE_CODE,
            slug,
            slug,
            name,
            null,
            releaseDate,
            null,
            rawAttributes,
            assets
        );
        return ParseResult.ofSet(dto);
    }

    private static String extractSlug(String url, String naturalKey) {
        if (naturalKey != null && !naturalKey.isBlank()) {
            return naturalKey.toLowerCase(Locale.ROOT);
        }
        if (url == null) {
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
     * Scans the children of {@code div.caja_set_info} grouping nodes between {@code <br>} markers.
     * Each group is dispatched to either {@link #handleFlagBlock(List, Map)} or
     * {@link #handleLabelledBlock(List, Map)}.
     */
    private static void extractInfoBlock(Element infoBlock, Map<String, String> names, Map<String, String> labelled) {
        List<Node> buffer = new ArrayList<>();
        for (Node child : infoBlock.childNodes()) {
            if (child instanceof Element el && "br".equalsIgnoreCase(el.tagName())) {
                flushBlock(buffer, names, labelled);
                buffer.clear();
                continue;
            }
            buffer.add(child);
        }
        flushBlock(buffer, names, labelled);
    }

    private static void flushBlock(List<Node> buffer, Map<String, String> names, Map<String, String> labelled) {
        if (buffer.isEmpty()) {
            return;
        }
        if (handleFlagBlock(buffer, names)) {
            return;
        }
        handleLabelledBlock(buffer, labelled);
    }

    /**
     * Recognises a localized-name block: {@code <img src=".../flag-XXX.png"> textNode}.
     * Returns {@code true} when the buffer was identified (and consumed) as a flag block.
     */
    private static boolean handleFlagBlock(List<Node> buffer, Map<String, String> names) {
        Element flagImg = null;
        for (Node node : buffer) {
            if (node instanceof Element el && "img".equalsIgnoreCase(el.tagName()) && el.attr("src").contains("flag-")) {
                flagImg = el;
                break;
            }
        }
        if (flagImg == null) {
            return false;
        }
        String src = flagImg.attr("src").toLowerCase(Locale.ROOT);
        String key = mapFlagToKey(src);
        if (key == null) {
            return false;
        }
        String value = collectPlainText(buffer, flagImg);
        if (!value.isBlank() && !names.containsKey(key)) {
            names.put(key, value);
        }
        return true;
    }

    private static String mapFlagToKey(String src) {
        if (src.contains(FLAG_EN)) return "name";
        if (src.contains(FLAG_ES)) return "nameES";
        if (src.contains(FLAG_DE)) return "nameDE";
        if (src.contains(FLAG_FR)) return "nameFR";
        return null;
    }

    /**
     * Recognises a label-value block: optional {@code <i>}, then {@code <strong>Label:</strong>},
     * followed by either an {@code <a>} (use its text) or plain text.
     */
    private static void handleLabelledBlock(List<Node> buffer, Map<String, String> labelled) {
        Element labelStrong = null;
        for (Node node : buffer) {
            if (node instanceof Element el && "strong".equalsIgnoreCase(el.tagName())) {
                labelStrong = el;
                break;
            }
        }
        if (labelStrong == null) {
            return;
        }
        String key = normalizeLabel(labelStrong.text());
        if (key == null || key.isBlank()) {
            return;
        }
        String value = extractValue(buffer, labelStrong);
        if (value != null && !value.isBlank() && !labelled.containsKey(key)) {
            labelled.put(key, value);
        }
    }

    /** "Theme:" -> "theme"; "Export Market:" -> "exportMarket"; etc. */
    private static String normalizeLabel(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        if (trimmed.endsWith(":")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        trimmed = trimmed.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String[] parts = trimmed.split("\\s+");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].toLowerCase(Locale.ROOT);
            if (i == 0) {
                out.append(p);
            } else {
                out.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
            }
        }
        return out.toString();
    }

    /**
     * Returns the value text for a labelled block. Preference order:
     * <ol>
     *     <li>The first non-edit {@code <a>} text (so {@code Sports}, {@code Standard Box}, {@code 2026}).</li>
     *     <li>Concatenated plain text after the {@code <strong>}, ignoring edit anchors and
     *         {@code span.sets-contador}.</li>
     * </ol>
     */
    private static String extractValue(List<Node> buffer, Element labelStrong) {
        boolean afterLabel = false;
        String anchorValue = null;
        String contadorValue = null;
        StringBuilder textValue = new StringBuilder();
        for (Node node : buffer) {
            if (!afterLabel) {
                if (node == labelStrong) {
                    afterLabel = true;
                }
                continue;
            }
            if (node instanceof Element el) {
                String tag = el.tagName().toLowerCase(Locale.ROOT);
                if ("a".equals(tag) && !isEditAnchor(el)) {
                    if (anchorValue == null) {
                        String t = el.text().trim();
                        if (!t.isEmpty()) {
                            anchorValue = t;
                        }
                    }
                } else if ("span".equals(tag) && el.hasClass("sets-contador")) {
                    if (contadorValue == null) {
                        contadorValue = stripOuterParens(el.text().trim());
                    }
                } else if ("a".equals(tag) || "i".equals(tag)) {
                    // ignore edit pencil/cross icons.
                } else {
                    String t = el.text().trim();
                    if (!t.isEmpty()) {
                        appendWithSpace(textValue, t);
                    }
                }
            } else if (node instanceof TextNode tn) {
                String t = tn.text().trim();
                if (!t.isEmpty()) {
                    appendWithSpace(textValue, t);
                }
            }
        }
        if (anchorValue != null) {
            return anchorValue;
        }
        String txt = textValue.toString().trim();
        if (!txt.isEmpty()) {
            return txt;
        }
        // Fallback for "discontinued: (n/a)" style blocks where the only readable token is the
        // grey hint span.
        return contadorValue != null && !contadorValue.isBlank() ? contadorValue : null;
    }

    private static String stripOuterParens(String value) {
        if (value == null) return null;
        String t = value.trim();
        if (t.startsWith("(") && t.endsWith(")") && t.length() >= 2) {
            t = t.substring(1, t.length() - 1).trim();
        }
        return t;
    }

    private static boolean isEditAnchor(Element a) {
        String href = a.attr("href");
        if (href != null && href.contains("/editor/")) {
            return true;
        }
        // Edit anchors only contain an icon (<i>) and no readable text.
        return a.text().trim().isEmpty();
    }

    private static void appendWithSpace(StringBuilder sb, String text) {
        if (sb.length() > 0) {
            sb.append(' ');
        }
        sb.append(text);
    }

    /** Collects plain text after the flag image, ignoring nested elements. */
    private static String collectPlainText(List<Node> buffer, Element flagImg) {
        boolean afterFlag = false;
        StringBuilder sb = new StringBuilder();
        for (Node node : buffer) {
            if (!afterFlag) {
                if (node == flagImg) {
                    afterFlag = true;
                }
                continue;
            }
            if (node instanceof TextNode tn) {
                String t = tn.text().trim();
                if (!t.isEmpty()) {
                    appendWithSpace(sb, t);
                }
            } else if (node instanceof Element el) {
                String t = el.text().trim();
                if (!t.isEmpty()) {
                    appendWithSpace(sb, t);
                }
            }
        }
        return sb.toString().trim();
    }

    private static List<String> extractTags(Document doc) {
        Elements anchors = doc.select("div.settags a");
        List<String> out = new ArrayList<>(anchors.size());
        Set<String> seen = new LinkedHashSet<>();
        for (Element a : anchors) {
            String t = a.text().trim();
            if (!t.isEmpty() && seen.add(t.toLowerCase(Locale.ROOT))) {
                out.add(t);
            }
        }
        return out;
    }

    /**
     * Returns the main image (from {@code div.thumb-wrap a}) followed by any other
     * {@code <a rel="lightbox[set]">} thumbnails. Each {@code href} is the full-size image; the
     * label is taken from the inner {@code <img title|alt>}.
     */
    private static List<NormalizedAssetDto> extractImages(Document doc) {
        List<NormalizedAssetDto> assets = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        int sortOrder = 0;

        Element mainAnchor = doc.selectFirst("div.thumb-wrap a[href]");
        if (mainAnchor != null) {
            sortOrder = addImage(assets, seen, mainAnchor, sortOrder);
        }

        for (Element a : doc.select("a[rel='lightbox[set]'][href]")) {
            if (mainAnchor != null && a == mainAnchor) {
                continue;
            }
            sortOrder = addImage(assets, seen, a, sortOrder);
        }
        return assets;
    }

    private static int addImage(List<NormalizedAssetDto> assets, Set<String> seen, Element anchor, int sortOrder) {
        String href = anchor.attr("abs:href");
        if (href.isBlank() || !seen.add(href)) {
            return sortOrder;
        }
        String label = labelFor(anchor);
        assets.add(new NormalizedAssetDto(href, SourceAssetKind.IMAGE, label, sortOrder));
        return sortOrder + 1;
    }

    private static String labelFor(Element anchor) {
        Element img = anchor.selectFirst("img");
        if (img != null) {
            String title = img.attr("title").trim();
            if (!title.isEmpty()) {
                return title;
            }
            String alt = img.attr("alt").trim();
            if (!alt.isEmpty()) {
                return alt;
            }
        }
        String title = anchor.attr("title").trim();
        return title.isEmpty() ? null : title;
    }

    private ObjectNode buildRawAttributes(Map<String, String> names, Map<String, String> labelled, List<String> tags) {
        ObjectNode node = objectMapper.createObjectNode();

        // Names except the canonical one; English name is promoted to MegaSet.name.
        for (Map.Entry<String, String> entry : names.entrySet()) {
            if ("name".equals(entry.getKey())) {
                continue;
            }
            node.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : labelled.entrySet()) {
            if ("name".equals(entry.getKey()) || RELEASED_KEY.equals(entry.getKey())) {
                continue;
            }
            node.put(entry.getKey(), entry.getValue());
        }
        if (!tags.isEmpty()) {
            ArrayNode array = node.putArray("tags");
            for (String tag : tags) {
                array.add(tag);
            }
        }
        return node;
    }

    /**
     * Parses the {@code released} attribute. Klickypedia typically renders this as a 4-digit year
     * (e.g. {@code 2026}); we also accept ISO {@code yyyy-MM-dd} just in case. Year-only values are
     * normalized to January 1st of that year. Returns {@code null} when nothing parseable is found.
     */
    private static LocalDate parseReleaseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        Matcher full = FULL_DATE_PATTERN.matcher(value);
        if (full.find()) {
            try {
                return LocalDate.parse(full.group(0), ISO_DATE);
            } catch (Exception ignore) {
                // fall through to year matching
            }
        }
        Matcher year = YEAR_PATTERN.matcher(value);
        if (year.find()) {
            try {
                return LocalDate.of(Integer.parseInt(year.group(1)), 1, 1);
            } catch (NumberFormatException ignore) {
                return null;
            }
        }
        return null;
    }

    private static String firstNonBlank(String... candidates) {
        for (String c : candidates) {
            if (c != null && !c.isBlank()) {
                return c.trim();
            }
        }
        return null;
    }
}
