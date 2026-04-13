package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nookx.api.domain.enumeration.AttributeType;
import com.nookx.api.domain.enumeration.UIComponent;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * A MegaAttribute.
 */
@Entity
@Table(name = "mega_attribute")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaAttribute implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "ui_component")
    private UIComponent uiComponent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AttributeType type;

    @Column(name = "required")
    private Boolean required;

    @Column(name = "multiple")
    private Boolean multiple;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "min_number")
    private Double minNumber;

    @Column(name = "max_number")
    private Double maxNumber;

    @Column(name = "min_length")
    private Integer minLength;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "regex")
    private String regex;

    @Column(name = "jhi_order")
    private Integer order;

    @Column(name = "attribute_group")
    private String attributeGroup;

    @Column(name = "active")
    private Boolean active;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "attributes")
    @JsonIgnoreProperties(value = { "attributes" }, allowSetters = true)
    @Setter(AccessLevel.NONE)
    private Set<MegaSetType> setTypes = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "attributes")
    @JsonIgnoreProperties(value = { "attributes" }, allowSetters = true)
    @Setter(AccessLevel.NONE)
    private Set<MegaPartType> partTypes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public MegaAttribute id(Long id) {
        this.setId(id);
        return this;
    }

    public MegaAttribute name(String name) {
        this.setName(name);
        return this;
    }

    public MegaAttribute label(String label) {
        this.setLabel(label);
        return this;
    }

    public MegaAttribute description(String description) {
        this.setDescription(description);
        return this;
    }

    public MegaAttribute uiComponent(UIComponent uiComponent) {
        this.setUiComponent(uiComponent);
        return this;
    }

    public MegaAttribute type(AttributeType type) {
        this.setType(type);
        return this;
    }

    public MegaAttribute required(Boolean required) {
        this.setRequired(required);
        return this;
    }

    public MegaAttribute multiple(Boolean multiple) {
        this.setMultiple(multiple);
        return this;
    }

    public MegaAttribute defaultValue(String defaultValue) {
        this.setDefaultValue(defaultValue);
        return this;
    }

    public MegaAttribute minNumber(Double minNumber) {
        this.setMinNumber(minNumber);
        return this;
    }

    public MegaAttribute maxNumber(Double maxNumber) {
        this.setMaxNumber(maxNumber);
        return this;
    }

    public MegaAttribute minLength(Integer minLength) {
        this.setMinLength(minLength);
        return this;
    }

    public MegaAttribute maxLength(Integer maxLength) {
        this.setMaxLength(maxLength);
        return this;
    }

    public MegaAttribute regex(String regex) {
        this.setRegex(regex);
        return this;
    }

    public MegaAttribute order(Integer order) {
        this.setOrder(order);
        return this;
    }

    public MegaAttribute attributeGroup(String attributeGroup) {
        this.setAttributeGroup(attributeGroup);
        return this;
    }

    public MegaAttribute active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setSetTypes(Set<MegaSetType> megaSetTypes) {
        if (this.setTypes != null) {
            this.setTypes.forEach(i -> i.removeAttribute(this));
        }
        if (megaSetTypes != null) {
            megaSetTypes.forEach(i -> i.addAttribute(this));
        }
        this.setTypes = megaSetTypes;
    }

    public MegaAttribute setTypes(Set<MegaSetType> megaSetTypes) {
        this.setSetTypes(megaSetTypes);
        return this;
    }

    public MegaAttribute addSetType(MegaSetType megaSetType) {
        this.setTypes.add(megaSetType);
        megaSetType.getAttributes().add(this);
        return this;
    }

    public MegaAttribute removeSetType(MegaSetType megaSetType) {
        this.setTypes.remove(megaSetType);
        megaSetType.getAttributes().remove(this);
        return this;
    }

    public void setPartTypes(Set<MegaPartType> megaPartTypes) {
        if (this.partTypes != null) {
            this.partTypes.forEach(i -> i.removeAttribute(this));
        }
        if (megaPartTypes != null) {
            megaPartTypes.forEach(i -> i.addAttribute(this));
        }
        this.partTypes = megaPartTypes;
    }

    public MegaAttribute partTypes(Set<MegaPartType> megaPartTypes) {
        this.setPartTypes(megaPartTypes);
        return this;
    }

    public MegaAttribute addPartType(MegaPartType megaPartType) {
        this.partTypes.add(megaPartType);
        megaPartType.getAttributes().add(this);
        return this;
    }

    public MegaAttribute removePartType(MegaPartType megaPartType) {
        this.partTypes.remove(megaPartType);
        megaPartType.getAttributes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaAttribute)) {
            return false;
        }
        return getId() != null && getId().equals(((MegaAttribute) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaAttribute{" +
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
            "}";
    }
}
