package com.dot.collector.api.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dot.collector.api.domain.ProfileRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfileRequestDTO implements Serializable {

    private Long id;

    private String message;

    private ProfileRequestTypeDTO type;

    private ProfileDTO profile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProfileRequestTypeDTO getType() {
        return type;
    }

    public void setType(ProfileRequestTypeDTO type) {
        this.type = type;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }

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
