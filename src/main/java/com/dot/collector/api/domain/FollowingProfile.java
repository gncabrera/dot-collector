package com.dot.collector.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A FollowingProfile.
 */
@Entity
@Table(name = "following_profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
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

    public Long getId() {
        return this.id;
    }

    public FollowingProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateFollowing() {
        return this.dateFollowing;
    }

    public FollowingProfile dateFollowing(LocalDate dateFollowing) {
        this.setDateFollowing(dateFollowing);
        return this;
    }

    public void setDateFollowing(LocalDate dateFollowing) {
        this.dateFollowing = dateFollowing;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public FollowingProfile profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public Profile getFollowedProfile() {
        return this.followedProfile;
    }

    public void setFollowedProfile(Profile profile) {
        this.followedProfile = profile;
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
