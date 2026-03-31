package com.dot.collector.api.domain;

import com.dot.collector.api.domain.enumeration.AttributeType;
import com.dot.collector.api.domain.enumeration.UIComponent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A MegaAttribute.
 */
@Entity
@Table(name = "mega_attribute")
@SuppressWarnings("common-java:DuplicatedBlocks")
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
    private Set<MegaSetType> setTypes = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "attributes")
    @JsonIgnoreProperties(value = { "attributes" }, allowSetters = true)
    private Set<MegaPartType> partTypes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MegaAttribute id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MegaAttribute name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return this.label;
    }

    public MegaAttribute label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return this.description;
    }

    public MegaAttribute description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UIComponent getUiComponent() {
        return this.uiComponent;
    }

    public MegaAttribute uiComponent(UIComponent uiComponent) {
        this.setUiComponent(uiComponent);
        return this;
    }

    public void setUiComponent(UIComponent uiComponent) {
        this.uiComponent = uiComponent;
    }

    public AttributeType getType() {
        return this.type;
    }

    public MegaAttribute type(AttributeType type) {
        this.setType(type);
        return this;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return this.required;
    }

    public MegaAttribute required(Boolean required) {
        this.setRequired(required);
        return this;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getMultiple() {
        return this.multiple;
    }

    public MegaAttribute multiple(Boolean multiple) {
        this.setMultiple(multiple);
        return this;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public MegaAttribute defaultValue(String defaultValue) {
        this.setDefaultValue(defaultValue);
        return this;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Double getMinNumber() {
        return this.minNumber;
    }

    public MegaAttribute minNumber(Double minNumber) {
        this.setMinNumber(minNumber);
        return this;
    }

    public void setMinNumber(Double minNumber) {
        this.minNumber = minNumber;
    }

    public Double getMaxNumber() {
        return this.maxNumber;
    }

    public MegaAttribute maxNumber(Double maxNumber) {
        this.setMaxNumber(maxNumber);
        return this;
    }

    public void setMaxNumber(Double maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Integer getMinLength() {
        return this.minLength;
    }

    public MegaAttribute minLength(Integer minLength) {
        this.setMinLength(minLength);
        return this;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return this.maxLength;
    }

    public MegaAttribute maxLength(Integer maxLength) {
        this.setMaxLength(maxLength);
        return this;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public String getRegex() {
        return this.regex;
    }

    public MegaAttribute regex(String regex) {
        this.setRegex(regex);
        return this;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Integer getOrder() {
        return this.order;
    }

    public MegaAttribute order(Integer order) {
        this.setOrder(order);
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getAttributeGroup() {
        return this.attributeGroup;
    }

    public MegaAttribute attributeGroup(String attributeGroup) {
        this.setAttributeGroup(attributeGroup);
        return this;
    }

    public void setAttributeGroup(String attributeGroup) {
        this.attributeGroup = attributeGroup;
    }

    public Boolean getActive() {
        return this.active;
    }

    public MegaAttribute active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<MegaSetType> getSetTypes() {
        return this.setTypes;
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

    public Set<MegaPartType> getPartTypes() {
        return this.partTypes;
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
