package com.dot.collector.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A MegaSet.
 */
@Entity
@Table(name = "mega_set")
@SuppressWarnings("common-java:DuplicatedBlocks")
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
    @Column(name = "name_en", nullable = false)
    private String nameEN;

    @Column(name = "name_es")
    private String nameES;

    @Column(name = "name_de")
    private String nameDE;

    @Column(name = "name_fr")
    private String nameFR;

    @NotNull
    @Column(name = "description_en", nullable = false)
    private String descriptionEN;

    @Column(name = "description_es")
    private String descriptionES;

    @Column(name = "description_de")
    private String descriptionDE;

    @Column(name = "description_fr")
    private String descriptionFR;

    @Lob
    @Column(name = "attributes")
    private byte[] attributes;

    @Column(name = "attributes_content_type")
    private String attributesContentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "attributes" }, allowSetters = true)
    private MegaSetType type;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sets")
    @JsonIgnoreProperties(value = { "collection", "sets" }, allowSetters = true)
    private Set<ProfileCollectionSet> profileCollectionSets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MegaSet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetNumber() {
        return this.setNumber;
    }

    public MegaSet setNumber(String setNumber) {
        this.setSetNumber(setNumber);
        return this;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public LocalDate getReleaseDate() {
        return this.releaseDate;
    }

    public MegaSet releaseDate(LocalDate releaseDate) {
        this.setReleaseDate(releaseDate);
        return this;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getNotes() {
        return this.notes;
    }

    public MegaSet notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNameEN() {
        return this.nameEN;
    }

    public MegaSet nameEN(String nameEN) {
        this.setNameEN(nameEN);
        return this;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getNameES() {
        return this.nameES;
    }

    public MegaSet nameES(String nameES) {
        this.setNameES(nameES);
        return this;
    }

    public void setNameES(String nameES) {
        this.nameES = nameES;
    }

    public String getNameDE() {
        return this.nameDE;
    }

    public MegaSet nameDE(String nameDE) {
        this.setNameDE(nameDE);
        return this;
    }

    public void setNameDE(String nameDE) {
        this.nameDE = nameDE;
    }

    public String getNameFR() {
        return this.nameFR;
    }

    public MegaSet nameFR(String nameFR) {
        this.setNameFR(nameFR);
        return this;
    }

    public void setNameFR(String nameFR) {
        this.nameFR = nameFR;
    }

    public String getDescriptionEN() {
        return this.descriptionEN;
    }

    public MegaSet descriptionEN(String descriptionEN) {
        this.setDescriptionEN(descriptionEN);
        return this;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public String getDescriptionES() {
        return this.descriptionES;
    }

    public MegaSet descriptionES(String descriptionES) {
        this.setDescriptionES(descriptionES);
        return this;
    }

    public void setDescriptionES(String descriptionES) {
        this.descriptionES = descriptionES;
    }

    public String getDescriptionDE() {
        return this.descriptionDE;
    }

    public MegaSet descriptionDE(String descriptionDE) {
        this.setDescriptionDE(descriptionDE);
        return this;
    }

    public void setDescriptionDE(String descriptionDE) {
        this.descriptionDE = descriptionDE;
    }

    public String getDescriptionFR() {
        return this.descriptionFR;
    }

    public MegaSet descriptionFR(String descriptionFR) {
        this.setDescriptionFR(descriptionFR);
        return this;
    }

    public void setDescriptionFR(String descriptionFR) {
        this.descriptionFR = descriptionFR;
    }

    public byte[] getAttributes() {
        return this.attributes;
    }

    public MegaSet attributes(byte[] attributes) {
        this.setAttributes(attributes);
        return this;
    }

    public void setAttributes(byte[] attributes) {
        this.attributes = attributes;
    }

    public String getAttributesContentType() {
        return this.attributesContentType;
    }

    public MegaSet attributesContentType(String attributesContentType) {
        this.attributesContentType = attributesContentType;
        return this;
    }

    public void setAttributesContentType(String attributesContentType) {
        this.attributesContentType = attributesContentType;
    }

    public MegaSetType getType() {
        return this.type;
    }

    public void setType(MegaSetType megaSetType) {
        this.type = megaSetType;
    }

    public MegaSet type(MegaSetType megaSetType) {
        this.setType(megaSetType);
        return this;
    }

    public Set<ProfileCollectionSet> getProfileCollectionSets() {
        return this.profileCollectionSets;
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
            ", nameEN='" + getNameEN() + "'" +
            ", nameES='" + getNameES() + "'" +
            ", nameDE='" + getNameDE() + "'" +
            ", nameFR='" + getNameFR() + "'" +
            ", descriptionEN='" + getDescriptionEN() + "'" +
            ", descriptionES='" + getDescriptionES() + "'" +
            ", descriptionDE='" + getDescriptionDE() + "'" +
            ", descriptionFR='" + getDescriptionFR() + "'" +
            ", attributes='" + getAttributes() + "'" +
            ", attributesContentType='" + getAttributesContentType() + "'" +
            "}";
    }
}
