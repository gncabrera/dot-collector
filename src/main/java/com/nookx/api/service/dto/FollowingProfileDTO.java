package com.nookx.api.service.dto;

import com.nookx.api.domain.FollowingProfile;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link FollowingProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class FollowingProfileDTO implements Serializable {

    private Long id;

    private LocalDate dateFollowing;

    private ProfileDTO profile;

    private ProfileDTO followedProfile;

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
