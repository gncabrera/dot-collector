package com.nookx.api.service.dto;

import com.nookx.api.domain.ProfileRequest;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link ProfileRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProfileRequestDTO implements Serializable {

    private Long id;

    private String message;

    private ProfileRequestTypeDTO type;

    private ProfileDTO profile;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileRequestDTO)) {
            return false;
        }

        ProfileRequestDTO profileRequestDTO = (ProfileRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, profileRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileRequestDTO{" +
            "id=" + getId() +
            ", message='" + getMessage() + "'" +
            ", type=" + getType() +
            ", profile=" + getProfile() +
            "}";
    }
}
