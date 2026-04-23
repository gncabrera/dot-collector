package com.nookx.api.scraper.domain;

import com.nookx.api.scraper.domain.enumeration.FetchStatus;
import com.nookx.api.scraper.domain.enumeration.PageType;
import com.nookx.api.scraper.domain.enumeration.ParseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * Operational tracking row for every URL the scraper cares about.
 * <p>
 * Acts as both the work queue (via {@link FetchStatus#PENDING} / {@code next_check_at}) and
 * the durable log of everything fetched from each source. The natural key is
 * {@code (source_code, url)}; {@code natural_key} holds the source-specific id
 * (e.g. setNumber) for quick lookups.
 */
@Entity
@Table(
    name = "scrape_page",
    uniqueConstraints = { @UniqueConstraint(name = "ux_scrape_page__source_url", columnNames = { "source_code", "url" }) },
    indexes = {
        @Index(name = "ix_scrape_page__fetch_next", columnList = "fetch_status, next_check_at"),
        @Index(name = "ix_scrape_page__parse_status", columnList = "parse_status"),
        @Index(name = "ix_scrape_page__source_type_key", columnList = "source_code, page_type, natural_key"),
    }
)
@Getter
@Setter
public class ScrapePage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "source_code", nullable = false, length = 64)
    private String sourceCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "page_type", nullable = false, length = 32)
    private PageType pageType;

    @NotNull
    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    /** Source-specific natural id (setNumber, partNumber, themeName, ...). Null for index pages. */
    @Column(name = "natural_key", length = 255)
    private String naturalKey;

    /** Relative path under {@code application.scraper.raw-storage-directory} once fetched. */
    @Column(name = "storage_path", length = 512)
    private String storagePath;

    /** HTTP status of the last fetch attempt. */
    @Column(name = "http_status")
    private Integer httpStatus;

    /** HTTP ETag header, used for If-None-Match on subsequent fetches. */
    @Column(name = "etag", length = 255)
    private String etag;

    /** HTTP Last-Modified header, used for If-Modified-Since on subsequent fetches. */
    @Column(name = "last_modified", length = 64)
    private String lastModified;

    /** SHA-256 of the downloaded body. Used to skip re-parse when content is unchanged. */
    @Column(name = "content_hash", length = 64)
    private String contentHash;

    /** Size in bytes of the last stored payload. */
    @Column(name = "content_size_bytes")
    private Long contentSizeBytes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "fetch_status", nullable = false, length = 32)
    private FetchStatus fetchStatus = FetchStatus.PENDING;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "parse_status", nullable = false, length = 32)
    private ParseStatus parseStatus = ParseStatus.PENDING;

    @Column(name = "discovered_at")
    private Instant discoveredAt;

    @Column(name = "fetched_at")
    private Instant fetchedAt;

    @Column(name = "parsed_at")
    private Instant parsedAt;

    /** When this page should be looked at again by the fetch runner. */
    @Column(name = "next_check_at")
    private Instant nextCheckAt;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "last_error", length = 2000)
    private String lastError;

    @PrePersist
    void onCreate() {
        if (discoveredAt == null) {
            discoveredAt = Instant.now();
        }
        if (nextCheckAt == null) {
            nextCheckAt = discoveredAt;
        }
    }
}
