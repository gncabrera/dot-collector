package com.dot.collector.api.domain;

import com.dot.collector.api.domain.enumeration.AssetType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A MegaAsset.
 */
@Entity
@Table(name = "mega_asset")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MegaAsset implements Serializable {

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

    @NotNull
    @Column(name = "path", nullable = false)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AssetType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "type", "profileCollectionSets" }, allowSetters = true)
    private MegaSet set;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "type", "partCategory", "partSubCategories" }, allowSetters = true)
    private MegaPart part;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MegaAsset id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public MegaAsset name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public MegaAsset description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return this.path;
    }

    public MegaAsset path(String path) {
        this.setPath(path);
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public AssetType getType() {
        return this.type;
    }

    public MegaAsset type(AssetType type) {
        this.setType(type);
        return this;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public MegaSet getSet() {
        return this.set;
    }

    public void setSet(MegaSet megaSet) {
        this.set = megaSet;
    }

    public MegaAsset set(MegaSet megaSet) {
        this.setSet(megaSet);
        return this;
    }

    public MegaPart getPart() {
        return this.part;
    }

    public void setPart(MegaPart megaPart) {
        this.part = megaPart;
    }

    public MegaAsset part(MegaPart megaPart) {
        this.setPart(megaPart);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaAsset)) {
            return false;
        }
        return getId() != null && getId().equals(((MegaAsset) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaAsset{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", path='" + getPath() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
