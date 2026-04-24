package com.nookx.api.scraper.source.klickypedia;

import com.nookx.api.scraper.api.CatalogSource;
import com.nookx.api.scraper.api.PageParser;
import com.nookx.api.scraper.api.SourceDiscoverer;
import com.nookx.api.scraper.domain.enumeration.PageType;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Klickypedia plugin entry point.
 * <p>
 * Registered only when the scraper profile is active AND the source is explicitly enabled in
 * {@code application.scraper.sources.klickypedia.enabled}.
 */
@Component
@Profile("scraper")
@ConditionalOnProperty(prefix = "application.scraper.sources.klickypedia", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KlickypediaSource implements CatalogSource {

    private final SourceDiscoverer discoverer;
    private final Map<PageType, PageParser> parsers;

    public KlickypediaSource(
        KlickypediaDiscoverer discoverer,
        KlickypediaSetListParser setListParser,
        KlickypediaSetDetailParser setDetailParser
    ) {
        this.discoverer = discoverer;
        this.parsers = Map.of(setListParser.pageType(), setListParser, setDetailParser.pageType(), setDetailParser);
    }

    @Override
    public String sourceCode() {
        return KlickypediaConstants.SOURCE_CODE;
    }

    @Override
    public SourceDiscoverer discoverer() {
        return discoverer;
    }

    @Override
    public Optional<PageParser> parserFor(PageType pageType) {
        return Optional.ofNullable(parsers.get(pageType));
    }
}
