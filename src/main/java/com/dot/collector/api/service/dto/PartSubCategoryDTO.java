package com.dot.collector.api.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.dot.collector.api.domain.PartSubCategory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartSubCategoryDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private Set<MegaPartDTO> megaParts = new HashSet<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<MegaPartDTO> getMegaParts() {
        return megaParts;
    }

    public void setMegaParts(Set<MegaPartDTO> megaParts) {
        this.megaParts = megaParts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartSubCategoryDTO)) {
            return false;
        }

        PartSubCategoryDTO partSubCategoryDTO = (PartSubCategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, partSubCategoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartSubCategoryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", megaParts=" + getMegaParts() +
            "}";
    }
}
