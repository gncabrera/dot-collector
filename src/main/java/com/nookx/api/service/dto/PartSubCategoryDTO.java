package com.nookx.api.service.dto;

import com.nookx.api.domain.PartSubCategory;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link PartSubCategory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class PartSubCategoryDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private Set<MegaPartDTO> megaParts = new HashSet<>();

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
