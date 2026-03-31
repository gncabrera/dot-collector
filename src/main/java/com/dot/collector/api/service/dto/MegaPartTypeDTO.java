package com.dot.collector.api.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaPartType} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MegaPartTypeDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Integer version;

    private Boolean active;

    private Boolean isLatest;

    private Set<MegaAttributeDTO> attributes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getIsLatest() {
        return isLatest;
    }

    public void setIsLatest(Boolean isLatest) {
        this.isLatest = isLatest;
    }

    public Set<MegaAttributeDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<MegaAttributeDTO> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaPartTypeDTO)) {
            return false;
        }

        MegaPartTypeDTO megaPartTypeDTO = (MegaPartTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaPartTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaPartTypeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", version=" + getVersion() +
            ", active='" + getActive() + "'" +
            ", isLatest='" + getIsLatest() + "'" +
            ", attributes=" + getAttributes() +
            "}";
    }
}
