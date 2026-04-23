package com.nookx.api.scraper.domain;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.scraper.domain.enumeration.SourceAssetKind;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * Staging row for an asset (image/PDF) referenced by a {@link SourceSet}.
 * <p>
 * Rows are created by the parser as soon as the asset URL is discovered. The asset download itself
 * happens asynchronously by {@code AssetFetchRunner}; once downloaded, {@link #megaAsset} is set.
 */
@Entity
@Table(
    name = "source_set_asset",
    uniqueConstraints = { @UniqueConstraint(name = "ux_source_set_asset__set_url", columnNames = { "source_set_id", "external_url" }) },
    indexes = {
        @Index(name = "ix_source_set_asset__download", columnList = "downloaded, kind"),
        @Index(name = "ix_source_set_asset__content_hash", columnList = "content_hash"),
    }
)
@Getter
@Setter
public class SourceSetAsset implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_set_id", nullable = false)
    private SourceSet sourceSet;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 32)
    private SourceAssetKind kind;

    /** Absolute URL of the asset at the source. Unique per SourceSet. */
    @NotNull
    @Column(name = "external_url", nullable = false, length = 2048)
    private String externalUrl;

    /** Optional human label as provided by the source (e.g. "box front", "instructions part 1"). */
    @Column(name = "label", length = 255)
    private String label;

    /** Optional ordering hint to preserve source order on the canonical side. */
    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "downloaded", nullable = false)
    private boolean downloaded = false;

    @Column(name = "download_failed", nullable = false)
    private boolean downloadFailed = false;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    /** SHA-256 of the downloaded bytes. Used to dedupe across sets. */
    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @Column(name = "content_size_bytes")
    private Long contentSizeBytes;

    @Column(name = "content_type", length = 255)
    private String contentType;

    /** Canonical asset row once the binary is persisted. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mega_asset_id")
    private MegaAsset megaAsset;

    @Column(name = "last_tried_at")
    private Instant lastTriedAt;

    @Column(name = "downloaded_at")
    private Instant downloadedAt;

    @Column(name = "last_error", length = 2000)
    private String lastError;
}
