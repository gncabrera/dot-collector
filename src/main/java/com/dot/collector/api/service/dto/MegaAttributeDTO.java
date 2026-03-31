package com.dot.collector.api.service.dto;

import com.dot.collector.api.domain.enumeration.AttributeType;
import com.dot.collector.api.domain.enumeration.UIComponent;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaAttribute} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UIComponent getUiComponent() {
        return uiComponent;
    }

    public void setUiComponent(UIComponent uiComponent) {
        this.uiComponent = uiComponent;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getMultiple() {
        return multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Double getMinNumber() {
        return minNumber;
    }

    public void setMinNumber(Double minNumber) {
        this.minNumber = minNumber;
    }

    public Double getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Double maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getAttributeGroup() {
        return attributeGroup;
    }

    public void setAttributeGroup(String attributeGroup) {
        this.attributeGroup = attributeGroup;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<MegaSetTypeDTO> getSetTypes() {
        return setTypes;
    }

    public void setSetTypes(Set<MegaSetTypeDTO> setTypes) {
        this.setTypes = setTypes;
    }

    public Set<MegaPartTypeDTO> getPartTypes() {
        return partTypes;
    }

    public void setPartTypes(Set<MegaPartTypeDTO> partTypes) {
        this.partTypes = partTypes;
    }

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
