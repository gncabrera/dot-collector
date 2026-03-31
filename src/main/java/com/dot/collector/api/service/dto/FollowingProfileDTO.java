package com.dot.collector.api.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.dot.collector.api.domain.FollowingProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FollowingProfileDTO implements Serializable {

    private Long id;

    private LocalDate dateFollowing;

    private ProfileDTO profile;

    private ProfileDTO followedProfile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateFollowing() {
        return dateFollowing;
    }

    public void setDateFollowing(LocalDate dateFollowing) {
        this.dateFollowing = dateFollowing;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }

    public ProfileDTO getFollowedProfile() {
        return followedProfile;
    }

    public void setFollowedProfile(ProfileDTO followedProfile) {
        this.followedProfile = followedProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FollowingProfileDTO)) {
            return false;
        }

        FollowingProfileDTO followingProfileDTO = (FollowingProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, followingProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FollowingProfileDTO{" +
            "id=" + getId() +
            ", dateFollowing='" + getDateFollowing() + "'" +
            ", profile=" + getProfile() +
            ", followedProfile=" + getFollowedProfile() +
            "}";
    }
}
