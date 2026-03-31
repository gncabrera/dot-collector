package com.dot.collector.api.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaAttributeOption} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MegaAttributeOptionDTO implements Serializable {

    private Long id;

    private String label;

    private String value;

    private String description;

    private MegaAttributeDTO attribute;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MegaAttributeDTO getAttribute() {
        return attribute;
    }

    public void setAttribute(MegaAttributeDTO attribute) {
        this.attribute = attribute;
    }

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
