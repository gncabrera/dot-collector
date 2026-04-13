package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * A PartSubCategory.
 */
@Entity
@Table(name = "part_sub_category")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class PartSubCategory implements Serializable {

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

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "partSubCategories")
    @JsonIgnoreProperties(value = { "type", "partCategory", "partSubCategories" }, allowSetters = true)
    @Setter(AccessLevel.NONE)
    private Set<MegaPart> megaParts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public PartSubCategory id(Long id) {
        this.setId(id);
        return this;
    }

    public PartSubCategory name(String name) {
        this.setName(name);
        return this;
    }

    public PartSubCategory description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setMegaParts(Set<MegaPart> megaParts) {
        if (this.megaParts != null) {
            this.megaParts.forEach(i -> i.removePartSubCategory(this));
        }
        if (megaParts != null) {
            megaParts.forEach(i -> i.addPartSubCategory(this));
        }
        this.megaParts = megaParts;
    }

    public PartSubCategory megaParts(Set<MegaPart> megaParts) {
        this.setMegaParts(megaParts);
        return this;
    }

    public PartSubCategory addMegaPart(MegaPart megaPart) {
        this.megaParts.add(megaPart);
        megaPart.getPartSubCategories().add(this);
        return this;
    }

    public PartSubCategory removeMegaPart(MegaPart megaPart) {
        this.megaParts.remove(megaPart);
        megaPart.getPartSubCategories().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartSubCategory)) {
            return false;
        }
        return getId() != null && getId().equals(((PartSubCategory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartSubCategory{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
