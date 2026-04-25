package com.nookx.api.scraper.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Staging row for a set as extracted from a specific source.
 * <p>
 * One row per {@code (source_code, source_external_id)}. Holds the normalized view of what the
 * parser produced, plus a {@code raw_attributes} JSON with every source-specific field that does
 * not fit in typed columns. Canonical merging into {@code mega_set} happens downstream via
 * {@link MegaSetSourceLink}.
 */
@Entity
@Table(
    name = "source_set",
    uniqueConstraints = {
        @UniqueConstraint(name = "ux_source_set__source_external", columnNames = { "source_code", "source_external_id" }),
    },
    indexes = { @Index(name = "ix_source_set__scrape_page", columnList = "scrape_page_id") }
)
@Getter
@Setter
public class SourceSet implements Serializable {

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
    @Column(name = "source_external_id", nullable = false, length = 255)
    private String sourceExternalId;

    /** Canonical setNumber as advertised by the source (usually equals the external id). */
    @Column(name = "set_number", length = 255)
    private String setNumber;

    @Column(name = "name", length = 1024)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    /** Theme / category label as provided by the source (e.g. "Knights", "Western"). */
    @Column(name = "theme", length = 255)
    private String theme;

    /** All additional source-specific fields, serialized as JSON. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_attributes", columnDefinition = "jsonb")
    private JsonNode rawAttributes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrape_page_id")
    private ScrapePage scrapePage;

    @Column(name = "first_seen_at")
    private Instant firstSeenAt;

    @Column(name = "last_parsed_at")
    private Instant lastParsedAt;

    @PrePersist
    void onCreate() {
        if (firstSeenAt == null) {
            firstSeenAt = Instant.now();
        }
    }
}
