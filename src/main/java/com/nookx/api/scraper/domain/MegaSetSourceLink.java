package com.nookx.api.scraper.domain;

import com.nookx.api.domain.MegaSet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * Bridge between a canonical {@link MegaSet} and its originating source.
 * <p>
 * Unique per {@code (source_code, source_external_id)}: a given source provides at most one set
 * with the same external id. A canonical {@link MegaSet} may link to one or many sources when
 * additional plugins are registered (e.g. Brickset + Rebrickable for the same LEGO set).
 */
@Entity
@Table(
    name = "mega_set_source_link",
    uniqueConstraints = {
        @UniqueConstraint(name = "ux_mega_set_source_link__source_external", columnNames = { "source_code", "source_external_id" }),
    },
    indexes = { @Index(name = "ix_mega_set_source_link__mega_set", columnList = "mega_set_id") }
)
@Getter
@Setter
public class MegaSetSourceLink implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mega_set_id", nullable = false)
    private MegaSet megaSet;

    @NotNull
    @Column(name = "source_code", nullable = false, length = 64)
    private String sourceCode;

    @NotNull
    @Column(name = "source_external_id", nullable = false, length = 255)
    private String sourceExternalId;

    @Column(name = "first_merged_at")
    private Instant firstMergedAt;

    @Column(name = "last_merged_at")
    private Instant lastMergedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (firstMergedAt == null) {
            firstMergedAt = now;
        }
        lastMergedAt = now;
    }
}
