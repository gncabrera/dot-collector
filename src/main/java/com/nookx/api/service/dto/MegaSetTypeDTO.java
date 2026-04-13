package com.nookx.api.service.dto;

import com.nookx.api.domain.MegaSetType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link MegaSetType} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaSetTypeDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Integer version;

    private Boolean active;

    private Boolean isLatest;

    private Set<MegaAttributeDTO> attributes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaSetTypeDTO)) {
            return false;
        }

        MegaSetTypeDTO megaSetTypeDTO = (MegaSetTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaSetTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaSetTypeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", version=" + getVersion() +
            ", active='" + getActive() + "'" +
            ", isLatest='" + getIsLatest() + "'" +
            ", attributes=" + getAttributes() +
            "}";
    }
}
