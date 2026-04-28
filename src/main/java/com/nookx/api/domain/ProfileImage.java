package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * Join between {@link Profile} and {@link MegaAsset} (at most one per profile).
 */
@Entity
@Table(name = "profile_image")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProfileImage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", unique = true, nullable = false)
    @JsonIgnore
    private Profile profile;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    @JsonIgnoreProperties(value = { "uploadedBy" }, allowSetters = true)
    private MegaAsset asset;

    public ProfileImage id(Long id) {
        this.setId(id);
        return this;
    }

    public ProfileImage profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public ProfileImage asset(MegaAsset asset) {
        this.setAsset(asset);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileImage)) {
            return false;
        }
        return getId() != null && getId().equals(((ProfileImage) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileImage{" + "id=" + getId() + "}";
    }
}
