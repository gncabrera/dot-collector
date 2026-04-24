package com.nookx.api.scraper.api;

import com.nookx.api.scraper.api.dto.DiscoveredUrl;
import com.nookx.api.scraper.api.dto.NormalizedSetDto;
import java.util.List;

/**
 * Output of a {@link PageParser}.
 * <p>
 * A parser may produce:
 * <ul>
 *     <li>{@code sets}: normalized set DTOs to upsert into staging / canonical tables.</li>
 *     <li>{@code newUrls}: URLs discovered inside the page that should be enqueued for future fetching
 *         (e.g. a SET_LIST page produces many SET_DETAIL URLs).</li>
 * </ul>
 * Lists are never null.
 */
public record ParseResult(List<NormalizedSetDto> sets, List<DiscoveredUrl> newUrls) {
    public ParseResult {
        if (sets == null) {
            sets = List.of();
        }
        if (newUrls == null) {
            newUrls = List.of();
        }
    }

    public static ParseResult empty() {
        return new ParseResult(List.of(), List.of());
    }

    public static ParseResult ofSet(NormalizedSetDto set) {
        return new ParseResult(List.of(set), List.of());
    }

    public static ParseResult ofUrls(List<DiscoveredUrl> urls) {
        return new ParseResult(List.of(), urls);
    }
}
