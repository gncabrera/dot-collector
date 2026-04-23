package com.nookx.api.scraper.core;

import com.nookx.api.config.ApplicationProperties;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Thin HTTP client used by every scraper fetcher. Centralizes:
 * <ul>
 *     <li>A browser-like User-Agent (configurable via {@code application.scraper.user-agent}).</li>
 *     <li>Conditional requests via ETag / Last-Modified so we produce HTTP 304 when nothing changed.</li>
 *     <li>Consistent error mapping into {@link FetchResult}.</li>
 * </ul>
 * <p>
 * The client itself does <b>not</b> enforce the rate limit: that is the runner's responsibility
 * (see {@code FetchRunner}). This keeps the client reusable from discovery, which can legitimately
 * make a handful of requests in a row when populating the queue for the first time.
 */
@Component
@Profile("scraper")
public class ScraperHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(ScraperHttpClient.class);

    private final ApplicationProperties.Scraper props;
    private final HttpClient httpClient;

    public ScraperHttpClient(ApplicationProperties applicationProperties) {
        this.props = applicationProperties.getScraper();
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(props.getConnectTimeoutMs()))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    public FetchResult get(String url) {
        return get(url, null, null);
    }

    /**
     * Performs a GET request with optional conditional headers.
     * <p>
     * When both {@code etag} and {@code lastModified} are non-null, both {@code If-None-Match} and
     * {@code If-Modified-Since} are sent; servers that honor them respond with HTTP 304 and
     * no body, which {@link FetchRunner} treats as {@code NOT_MODIFIED} without updating storage.
     */
    public FetchResult get(String url, String etag, String lastModified) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMillis(props.getRequestTimeoutMs()))
            .header("User-Agent", props.getUserAgent())
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
            .header("Accept-Language", "en-US,en;q=0.5")
            .GET();

        if (etag != null && !etag.isBlank()) {
            builder.header("If-None-Match", etag);
        }
        if (lastModified != null && !lastModified.isBlank()) {
            builder.header("If-Modified-Since", lastModified);
        }

        try {
            HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
            int status = response.statusCode();
            byte[] body = status == 304 ? null : response.body();
            String newEtag = response.headers().firstValue("ETag").orElse(null);
            String newLastModified = response.headers().firstValue("Last-Modified").orElse(null);
            String contentType = response.headers().firstValue("Content-Type").orElse(null);
            LOG.debug("GET {} -> {} ({} bytes)", url, status, body != null ? body.length : 0);
            return new FetchResult(status, body, contentType, newEtag, newLastModified, null);
        } catch (Exception ex) {
            LOG.warn("GET {} failed: {}", url, ex.toString());
            return FetchResult.transportError(ex.toString());
        }
    }
}
