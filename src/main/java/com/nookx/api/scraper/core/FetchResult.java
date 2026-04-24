package com.nookx.api.scraper.core;

/**
 * Outcome of a single HTTP fetch attempt performed by {@link ScraperHttpClient}.
 *
 * @param status        the HTTP status code received, or {@code 0} on transport failure
 * @param body          response body bytes ({@code null} on 304 Not Modified or on transport failure)
 * @param contentType   {@code Content-Type} response header, if any
 * @param etag          {@code ETag} response header, if any
 * @param lastModified  {@code Last-Modified} response header, if any
 * @param errorMessage  populated when {@link #isTransportError()} returns {@code true}
 */
public record FetchResult(int status, byte[] body, String contentType, String etag, String lastModified, String errorMessage) {
    public boolean isOk() {
        return status >= 200 && status < 300 && body != null;
    }

    public boolean isNotModified() {
        return status == 304;
    }

    public boolean isNotFound() {
        return status == 404 || status == 410;
    }

    public boolean isTransient() {
        return status >= 500 || status == 408 || status == 429;
    }

    public boolean isTransportError() {
        return status == 0;
    }

    public static FetchResult transportError(String message) {
        return new FetchResult(0, null, null, null, null, message);
    }
}
