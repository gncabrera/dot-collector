package com.nookx.api.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Nookx.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String baseUrl = "sarasa.update.me";

    private final Liquibase liquibase = new Liquibase();

    private final MegaAsset megaAsset = new MegaAsset();

    private final Scraper scraper = new Scraper();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    public MegaAsset getMegaAsset() {
        return megaAsset;
    }

    public Scraper getScraper() {
        return scraper;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // jhipster-needle-application-properties-property-getter

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    public static class MegaAsset {

        private String uploadDirectory = System.getProperty("java.io.tmpdir") + "/nookx-mega-assets";

        public String getUploadDirectory() {
            return uploadDirectory;
        }

        public void setUploadDirectory(String uploadDirectory) {
            this.uploadDirectory = uploadDirectory;
        }
    }

    /**
     * Configuration for the background web scraper.
     * <p>
     * Only active when the {@code scraper} Spring profile is enabled.
     */
    public static class Scraper {

        /** Local directory where raw downloaded HTML (gzipped) is stored. */
        private String rawStorageDirectory = "/data/scraper";

        /** User-Agent string sent with every HTTP request. Browser-like by default. */
        private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0";

        /** HTTP connect timeout in milliseconds. */
        private int connectTimeoutMs = 15_000;

        /** HTTP request timeout in milliseconds. */
        private int requestTimeoutMs = 60_000;

        /** Delay between fetch ticks (ms). 60s matches the agreed 1 req/min rate. */
        private long fetchDelayMs = 60_000;
        private long fetchInitialDelayMs = 60_000;

        public long getFetchInitialDelayMs() {
            return fetchInitialDelayMs;
        }

        public void setFetchInitialDelayMs(long fetchInitialDelayMs) {
            this.fetchInitialDelayMs = fetchInitialDelayMs;
        }

        public long getParseInitialDelayMs() {
            return parseInitialDelayMs;
        }

        public void setParseInitialDelayMs(long parseInitialDelayMs) {
            this.parseInitialDelayMs = parseInitialDelayMs;
        }

        public long getDiscoveryInitialDelayMs() {
            return discoveryInitialDelayMs;
        }

        public void setDiscoveryInitialDelayMs(long discoveryInitialDelayMs) {
            this.discoveryInitialDelayMs = discoveryInitialDelayMs;
        }

        public long getAssetFetchInitialDelayMs() {
            return assetFetchInitialDelayMs;
        }

        public void setAssetFetchInitialDelayMs(long assetFetchInitialDelayMs) {
            this.assetFetchInitialDelayMs = assetFetchInitialDelayMs;
        }

        /** Random jitter (+/-) added to the fetch delay to avoid robotic cadence. */
        private long fetchJitterMs = 15_000;

        /** How often the parser scans the local queue for pages pending parse. */
        private long parseDelayMs = 10_000;
        private long parseInitialDelayMs = 10_000;

        /** How often discovery runs (by default every 7 days). */
        private long discoveryDelayMs = 7L * 24 * 60 * 60 * 1000;
        private long discoveryInitialDelayMs = 7L * 24 * 60 * 60 * 1000;

        /** How often the asset downloader fetches one asset (same rate as FetchRunner). */
        private long assetFetchDelayMs = 60_000;
        private long assetFetchInitialDelayMs = 60_000;

        private final Recheck recheck = new Recheck();

        /** Per-source configuration keyed by {@link #sourceCode}. */
        private final Map<String, SourceConfig> sources = new LinkedHashMap<>();

        public String getRawStorageDirectory() {
            return rawStorageDirectory;
        }

        public void setRawStorageDirectory(String rawStorageDirectory) {
            this.rawStorageDirectory = rawStorageDirectory;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public int getConnectTimeoutMs() {
            return connectTimeoutMs;
        }

        public void setConnectTimeoutMs(int connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
        }

        public int getRequestTimeoutMs() {
            return requestTimeoutMs;
        }

        public void setRequestTimeoutMs(int requestTimeoutMs) {
            this.requestTimeoutMs = requestTimeoutMs;
        }

        public long getFetchDelayMs() {
            return fetchDelayMs;
        }

        public void setFetchDelayMs(long fetchDelayMs) {
            this.fetchDelayMs = fetchDelayMs;
        }

        public long getFetchJitterMs() {
            return fetchJitterMs;
        }

        public void setFetchJitterMs(long fetchJitterMs) {
            this.fetchJitterMs = fetchJitterMs;
        }

        public long getParseDelayMs() {
            return parseDelayMs;
        }

        public void setParseDelayMs(long parseDelayMs) {
            this.parseDelayMs = parseDelayMs;
        }

        public long getDiscoveryDelayMs() {
            return discoveryDelayMs;
        }

        public void setDiscoveryDelayMs(long discoveryDelayMs) {
            this.discoveryDelayMs = discoveryDelayMs;
        }

        public long getAssetFetchDelayMs() {
            return assetFetchDelayMs;
        }

        public void setAssetFetchDelayMs(long assetFetchDelayMs) {
            this.assetFetchDelayMs = assetFetchDelayMs;
        }

        public Recheck getRecheck() {
            return recheck;
        }

        public Map<String, SourceConfig> getSources() {
            return sources;
        }
    }

    /**
     * Re-check policy for already parsed pages. Values are configurable in YAML.
     */
    public static class Recheck {

        private int defaultDays = 90;
        private int recentSetReleaseDays = 14;
        private int recentSetAgeYears = 1;
        private int notFoundDays = 180;
        private int transientRetryMinutes = 60;
        private int maxRetries = 5;

        public int getDefaultDays() {
            return defaultDays;
        }

        public void setDefaultDays(int defaultDays) {
            this.defaultDays = defaultDays;
        }

        public int getRecentSetReleaseDays() {
            return recentSetReleaseDays;
        }

        public void setRecentSetReleaseDays(int recentSetReleaseDays) {
            this.recentSetReleaseDays = recentSetReleaseDays;
        }

        public int getRecentSetAgeYears() {
            return recentSetAgeYears;
        }

        public void setRecentSetAgeYears(int recentSetAgeYears) {
            this.recentSetAgeYears = recentSetAgeYears;
        }

        public int getNotFoundDays() {
            return notFoundDays;
        }

        public void setNotFoundDays(int notFoundDays) {
            this.notFoundDays = notFoundDays;
        }

        public int getTransientRetryMinutes() {
            return transientRetryMinutes;
        }

        public void setTransientRetryMinutes(int transientRetryMinutes) {
            this.transientRetryMinutes = transientRetryMinutes;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }
    }

    /**
     * Per-source configuration. Keyed by {@code sourceCode} in the {@code sources} map.
     */
    public static class SourceConfig {

        private boolean enabled = true;
        private String baseUrl;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    // jhipster-needle-application-properties-property-class
}
