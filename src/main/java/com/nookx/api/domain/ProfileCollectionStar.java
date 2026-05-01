package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/**
 * A ProfileCollectionStar.
 *
 * <p>Represents a star (favorite) given by a {@link Profile} to a public {@link ProfileCollection}.
 */
@Entity
@Table(
    name = "profile_collection_star",
    uniqueConstraints = @UniqueConstraint(
        name = "ux_profile_collection_star__profile_id_profile_collection_id",
        columnNames = { "profile_id", "profile_collection_id" }
    )
)
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProfileCollectionStar implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date_starred", nullable = false)
    private Instant dateStarred;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Profile profile;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_collection_id", nullable = false)
    @JsonIgnoreProperties(value = { "profile" }, allowSetters = true)
    private ProfileCollection profileCollection;

    public ProfileCollectionStar id(Long id) {
        this.setId(id);
        return this;
    }

    public ProfileCollectionStar dateStarred(Instant dateStarred) {
        this.setDateStarred(dateStarred);
        return this;
    }

    public ProfileCollectionStar profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public ProfileCollectionStar profileCollection(ProfileCollection profileCollection) {
        this.setProfileCollection(profileCollection);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileCollectionStar)) {
            return false;
        }
        return getId() != null && getId().equals(((ProfileCollectionStar) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ProfileCollectionStar{" + "id=" + getId() + ", dateStarred='" + getDateStarred() + "'" + "}";
    }
}
