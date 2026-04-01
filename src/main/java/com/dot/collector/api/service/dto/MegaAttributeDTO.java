package com.dot.collector.api.service.dto;

import com.dot.collector.api.domain.enumeration.AttributeType;
import com.dot.collector.api.domain.enumeration.UIComponent;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaAttribute} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaAttributeDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String label;

    private String description;

    private UIComponent uiComponent;

    @NotNull
    private AttributeType type;

    private Boolean required;

    private Boolean multiple;

    private String defaultValue;

    private Double minNumber;

    private Double maxNumber;

    private Integer minLength;

    private Integer maxLength;

    private String regex;

    private Integer order;

    private String attributeGroup;

    private Boolean active;

    private Set<MegaSetTypeDTO> setTypes = new HashSet<>();

    private Set<MegaPartTypeDTO> partTypes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaAttributeDTO)) {
            return false;
        }

        MegaAttributeDTO megaAttributeDTO = (MegaAttributeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaAttributeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaAttributeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", label='" + getLabel() + "'" +
            ", description='" + getDescription() + "'" +
            ", uiComponent='" + getUiComponent() + "'" +
            ", type='" + getType() + "'" +
            ", required='" + getRequired() + "'" +
            ", multiple='" + getMultiple() + "'" +
            ", defaultValue='" + getDefaultValue() + "'" +
            ", minNumber=" + getMinNumber() +
            ", maxNumber=" + getMaxNumber() +
            ", minLength=" + getMinLength() +
            ", maxLength=" + getMaxLength() +
            ", regex='" + getRegex() + "'" +
            ", order=" + getOrder() +
            ", attributeGroup='" + getAttributeGroup() + "'" +
            ", active='" + getActive() + "'" +
            ", setTypes=" + getSetTypes() +
            ", partTypes=" + getPartTypes() +
            "}";
    }
}
