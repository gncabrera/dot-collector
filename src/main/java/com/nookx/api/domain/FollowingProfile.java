package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A FollowingProfile.
 */
@Entity
@Table(name = "following_profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class FollowingProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "date_following")
    private LocalDate dateFollowing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Profile followedProfile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public FollowingProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public FollowingProfile dateFollowing(LocalDate dateFollowing) {
        this.setDateFollowing(dateFollowing);
        return this;
    }

    public FollowingProfile profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public FollowingProfile followedProfile(Profile profile) {
        this.setFollowedProfile(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FollowingProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((FollowingProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FollowingProfile{" +
            "id=" + getId() +
            ", dateFollowing='" + getDateFollowing() + "'" +
            "}";
    }
}
