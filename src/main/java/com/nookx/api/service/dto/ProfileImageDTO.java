package com.nookx.api.service.dto;

import com.nookx.api.domain.ProfileImage;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link ProfileImage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProfileImageDTO implements Serializable {

    private Long id;

    private ProfileDTO profile;

    private MegaAssetDTO asset;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileImageDTO)) {
            return false;
        }

        ProfileImageDTO profileImageDTO = (ProfileImageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, profileImageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileImageDTO{" + "id=" + getId() + ", profile=" + getProfile() + ", asset=" + getAsset() + "}";
    }
}
