package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * A MegaAttributeOption.
 */
@Entity
@Table(name = "mega_attribute_option")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaAttributeOption implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "label")
    private String label;

    @Column(name = "value")
    private String value;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "setTypes", "partTypes" }, allowSetters = true)
    private MegaAttribute attribute;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public MegaAttributeOption id(Long id) {
        this.setId(id);
        return this;
    }

    public MegaAttributeOption label(String label) {
        this.setLabel(label);
        return this;
    }

    public MegaAttributeOption value(String value) {
        this.setValue(value);
        return this;
    }

    public MegaAttributeOption description(String description) {
        this.setDescription(description);
        return this;
    }

    public MegaAttributeOption attribute(MegaAttribute megaAttribute) {
        this.setAttribute(megaAttribute);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaAttributeOption)) {
            return false;
        }
        return getId() != null && getId().equals(((MegaAttributeOption) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaAttributeOption{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
