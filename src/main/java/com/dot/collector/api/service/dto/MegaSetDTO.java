package com.dot.collector.api.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaSet} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MegaSetDTO implements Serializable {

    private Long id;

    @NotNull
    private String setNumber;

    private LocalDate releaseDate;

    private String notes;

    @NotNull
    private String nameEN;

    private String nameES;

    private String nameDE;

    private String nameFR;

    @NotNull
    private String descriptionEN;

    private String descriptionES;

    private String descriptionDE;

    private String descriptionFR;

    @Lob
    private byte[] attributes;

    private String attributesContentType;

    private MegaSetTypeDTO type;

    private Set<ProfileCollectionSetDTO> profileCollectionSets = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getNameES() {
        return nameES;
    }

    public void setNameES(String nameES) {
        this.nameES = nameES;
    }

    public String getNameDE() {
        return nameDE;
    }

    public void setNameDE(String nameDE) {
        this.nameDE = nameDE;
    }

    public String getNameFR() {
        return nameFR;
    }

    public void setNameFR(String nameFR) {
        this.nameFR = nameFR;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public String getDescriptionES() {
        return descriptionES;
    }

    public void setDescriptionES(String descriptionES) {
        this.descriptionES = descriptionES;
    }

    public String getDescriptionDE() {
        return descriptionDE;
    }

    public void setDescriptionDE(String descriptionDE) {
        this.descriptionDE = descriptionDE;
    }

    public String getDescriptionFR() {
        return descriptionFR;
    }

    public void setDescriptionFR(String descriptionFR) {
        this.descriptionFR = descriptionFR;
    }

    public byte[] getAttributes() {
        return attributes;
    }

    public void setAttributes(byte[] attributes) {
        this.attributes = attributes;
    }

    public String getAttributesContentType() {
        return attributesContentType;
    }

    public void setAttributesContentType(String attributesContentType) {
        this.attributesContentType = attributesContentType;
    }

    public MegaSetTypeDTO getType() {
        return type;
    }

    public void setType(MegaSetTypeDTO type) {
        this.type = type;
    }

    public Set<ProfileCollectionSetDTO> getProfileCollectionSets() {
        return profileCollectionSets;
    }

    public void setProfileCollectionSets(Set<ProfileCollectionSetDTO> profileCollectionSets) {
        this.profileCollectionSets = profileCollectionSets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaSetDTO)) {
            return false;
        }

        MegaSetDTO megaSetDTO = (MegaSetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaSetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaSetDTO{" +
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
            ", type=" + getType() +
            ", profileCollectionSets=" + getProfileCollectionSets() +
            "}";
    }
}
