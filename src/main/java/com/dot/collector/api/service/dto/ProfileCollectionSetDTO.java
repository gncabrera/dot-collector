package com.dot.collector.api.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.dot.collector.api.domain.ProfileCollectionSet} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfileCollectionSetDTO implements Serializable {

    private Long id;

    private Boolean owned;

    private Boolean wanted;

    private LocalDate dateAdded;

    private ProfileCollectionDTO collection;

    private Set<MegaSetDTO> sets = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getOwned() {
        return owned;
    }

    public void setOwned(Boolean owned) {
        this.owned = owned;
    }

    public Boolean getWanted() {
        return wanted;
    }

    public void setWanted(Boolean wanted) {
        this.wanted = wanted;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public ProfileCollectionDTO getCollection() {
        return collection;
    }

    public void setCollection(ProfileCollectionDTO collection) {
        this.collection = collection;
    }

    public Set<MegaSetDTO> getSets() {
        return sets;
    }

    public void setSets(Set<MegaSetDTO> sets) {
        this.sets = sets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileCollectionSetDTO)) {
            return false;
        }

        ProfileCollectionSetDTO profileCollectionSetDTO = (ProfileCollectionSetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, profileCollectionSetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileCollectionSetDTO{" +
            "id=" + getId() +
            ", owned='" + getOwned() + "'" +
            ", wanted='" + getWanted() + "'" +
            ", dateAdded='" + getDateAdded() + "'" +
            ", collection=" + getCollection() +
            ", sets=" + getSets() +
            "}";
    }
}
