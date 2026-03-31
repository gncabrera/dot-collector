package com.dot.collector.api.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaPart} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MegaPartDTO implements Serializable {

    private Long id;

    private LocalDate releaseDate;

    @NotNull
    private String partNumber;

    @NotNull
    private String nameEN;

    private String nameES;

    private String nameDE;

    private String nameFR;

    private String description;

    private String notes;

    @Lob
    private byte[] attributes;

    private String attributesContentType;

    private MegaPartTypeDTO type;

    private PartCategoryDTO partCategory;

    private Set<PartSubCategoryDTO> partSubCategories = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public MegaPartTypeDTO getType() {
        return type;
    }

    public void setType(MegaPartTypeDTO type) {
        this.type = type;
    }

    public PartCategoryDTO getPartCategory() {
        return partCategory;
    }

    public void setPartCategory(PartCategoryDTO partCategory) {
        this.partCategory = partCategory;
    }

    public Set<PartSubCategoryDTO> getPartSubCategories() {
        return partSubCategories;
    }

    public void setPartSubCategories(Set<PartSubCategoryDTO> partSubCategories) {
        this.partSubCategories = partSubCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaPartDTO)) {
            return false;
        }

        MegaPartDTO megaPartDTO = (MegaPartDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaPartDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaPartDTO{" +
            "id=" + getId() +
            ", releaseDate='" + getReleaseDate() + "'" +
            ", partNumber='" + getPartNumber() + "'" +
            ", nameEN='" + getNameEN() + "'" +
            ", nameES='" + getNameES() + "'" +
            ", nameDE='" + getNameDE() + "'" +
            ", nameFR='" + getNameFR() + "'" +
            ", description='" + getDescription() + "'" +
            ", notes='" + getNotes() + "'" +
            ", attributes='" + getAttributes() + "'" +
            ", type=" + getType() +
            ", partCategory=" + getPartCategory() +
            ", partSubCategories=" + getPartSubCategories() +
            "}";
    }
}
