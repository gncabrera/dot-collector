package com.nookx.api.config;

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

    public static class Scraper {

        private String apiKey = "";

        private String principalLogin = "admin";

        private int maxBatchSize = 200;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getPrincipalLogin() {
            return principalLogin;
        }

        public void setPrincipalLogin(String principalLogin) {
            this.principalLogin = principalLogin;
        }

        public int getMaxBatchSize() {
            return maxBatchSize;
        }

        public void setMaxBatchSize(int maxBatchSize) {
            this.maxBatchSize = maxBatchSize;
        }
    }

    // jhipster-needle-application-properties-property-class
}
