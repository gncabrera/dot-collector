package com.dot.collector.api.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dot.collector.api.domain.ProfileRequestType} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfileRequestTypeDTO implements Serializable {

    private Long id;

    private String key;

    private String name;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileRequestTypeDTO)) {
            return false;
        }

        ProfileRequestTypeDTO profileRequestTypeDTO = (ProfileRequestTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, profileRequestTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileRequestTypeDTO{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
