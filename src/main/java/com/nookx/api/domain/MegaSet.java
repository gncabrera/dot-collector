package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A MegaSet.
 */
@Entity
@Table(name = "mega_set")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaSet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "set_number", nullable = false, unique = true)
    private String setNumber;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "notes")
    private String notes;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private JsonNode attributes;

    @Column(name = "attributes_content_type")
    private String attributesContentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "attributes" }, allowSetters = true)
    private MegaSetType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "profileInterests" }, allowSetters = true)
    private Interest interest;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sets")
    @JsonIgnoreProperties(value = { "collection", "sets" }, allowSetters = true)
    @Setter(AccessLevel.NONE)
    private Set<ProfileCollectionSet> profileCollectionSets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public MegaSet id(Long id) {
        this.setId(id);
        return this;
    }

    public MegaSet setNumber(String setNumber) {
        this.setSetNumber(setNumber);
        return this;
    }

    public MegaSet releaseDate(LocalDate releaseDate) {
        this.setReleaseDate(releaseDate);
        return this;
    }

    public MegaSet notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public MegaSet name(String name) {
        this.setName(name);
        return this;
    }

    public MegaSet description(String description) {
        this.setDescription(description);
        return this;
    }

    public MegaSet attributes(JsonNode attributes) {
        this.setAttributes(attributes);
        return this;
    }

    public MegaSet attributesContentType(String attributesContentType) {
        this.attributesContentType = attributesContentType;
        return this;
    }

    public MegaSet type(MegaSetType megaSetType) {
        this.setType(megaSetType);
        return this;
    }

    public void setProfileCollectionSets(Set<ProfileCollectionSet> profileCollectionSets) {
        if (this.profileCollectionSets != null) {
            this.profileCollectionSets.forEach(i -> i.removeSet(this));
        }
        if (profileCollectionSets != null) {
            profileCollectionSets.forEach(i -> i.addSet(this));
        }
        this.profileCollectionSets = profileCollectionSets;
    }

    public MegaSet profileCollectionSets(Set<ProfileCollectionSet> profileCollectionSets) {
        this.setProfileCollectionSets(profileCollectionSets);
        return this;
    }

    public MegaSet addProfileCollectionSet(ProfileCollectionSet profileCollectionSet) {
        this.profileCollectionSets.add(profileCollectionSet);
        profileCollectionSet.getSets().add(this);
        return this;
    }

    public MegaSet removeProfileCollectionSet(ProfileCollectionSet profileCollectionSet) {
        this.profileCollectionSets.remove(profileCollectionSet);
        profileCollectionSet.getSets().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaSet)) {
            return false;
        }
        return getId() != null && getId().equals(((MegaSet) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaSet{" +
            "id=" + getId() +
            ", setNumber='" + getSetNumber() + "'" +
            ", releaseDate='" + getReleaseDate() + "'" +
            ", notes='" + getNotes() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", attributes='" + getAttributes() + "'" +
            ", attributesContentType='" + getAttributesContentType() + "'" +
            "}";
    }
}
