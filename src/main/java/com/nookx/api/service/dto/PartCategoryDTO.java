package com.nookx.api.service.dto;

import com.nookx.api.domain.PartCategory;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link PartCategory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class PartCategoryDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartCategoryDTO)) {
            return false;
        }

        PartCategoryDTO partCategoryDTO = (PartCategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, partCategoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartCategoryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
