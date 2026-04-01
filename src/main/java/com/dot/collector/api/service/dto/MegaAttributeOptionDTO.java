package com.dot.collector.api.service.dto;

import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaAttributeOption} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaAttributeOptionDTO implements Serializable {

    private Long id;

    private String label;

    private String value;

    private String description;

    private MegaAttributeDTO attribute;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaAttributeOptionDTO)) {
            return false;
        }

        MegaAttributeOptionDTO megaAttributeOptionDTO = (MegaAttributeOptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaAttributeOptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaAttributeOptionDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            ", attribute=" + getAttribute() +
            "}";
    }
}
