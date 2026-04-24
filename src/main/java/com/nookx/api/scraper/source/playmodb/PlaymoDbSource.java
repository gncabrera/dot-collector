package com.nookx.api.scraper.source.playmodb;

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
 * PlaymoDB plugin entry point.
 * <p>
 * Registered only when the scraper profile is active AND the source is explicitly enabled in
 * {@code application.scraper.sources.playmodb.enabled} (which defaults to {@code true} via the
 * {@code application-scraper.yml} baseline).
 */
@Component
@Profile("scraper")
@ConditionalOnProperty(prefix = "application.scraper.sources.playmodb", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PlaymoDbSource implements CatalogSource {

    private final SourceDiscoverer discoverer;
    private final Map<PageType, PageParser> parsers;

    public PlaymoDbSource(PlaymoDbDiscoverer discoverer, PlaymoDbSetDetailParser setDetailParser, PlaymoDbSetListParser setListParser) {
        this.discoverer = discoverer;
        this.parsers = Map.of(setDetailParser.pageType(), setDetailParser, setListParser.pageType(), setListParser);
    }

    @Override
    public String sourceCode() {
        return PlaymoDbConstants.SOURCE_CODE;
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
