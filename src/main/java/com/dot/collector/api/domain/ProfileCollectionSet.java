package com.dot.collector.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * A ProfileCollectionSet.
 */
@Entity
@Table(name = "profile_collection_set")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProfileCollectionSet implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "owned")
    private Boolean owned;

    @Column(name = "wanted")
    private Boolean wanted;

    @Column(name = "date_added")
    private LocalDate dateAdded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "profile" }, allowSetters = true)
    private ProfileCollection collection;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_profile_collection_set__set",
        joinColumns = @JoinColumn(name = "profile_collection_set_id"),
        inverseJoinColumns = @JoinColumn(name = "set_id")
    )
    @JsonIgnoreProperties(value = { "type", "profileCollectionSets" }, allowSetters = true)
    private Set<MegaSet> sets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ProfileCollectionSet id(Long id) {
        this.setId(id);
        return this;
    }

    public ProfileCollectionSet owned(Boolean owned) {
        this.setOwned(owned);
        return this;
    }

    public ProfileCollectionSet wanted(Boolean wanted) {
        this.setWanted(wanted);
        return this;
    }

    public ProfileCollectionSet dateAdded(LocalDate dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public ProfileCollectionSet collection(ProfileCollection profileCollection) {
        this.setCollection(profileCollection);
        return this;
    }

    public ProfileCollectionSet sets(Set<MegaSet> megaSets) {
        this.setSets(megaSets);
        return this;
    }

    public ProfileCollectionSet addSet(MegaSet megaSet) {
        this.sets.add(megaSet);
        return this;
    }

    public ProfileCollectionSet removeSet(MegaSet megaSet) {
        this.sets.remove(megaSet);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileCollectionSet)) {
            return false;
        }
        return getId() != null && getId().equals(((ProfileCollectionSet) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileCollectionSet{" +
            "id=" + getId() +
            ", owned='" + getOwned() + "'" +
            ", wanted='" + getWanted() + "'" +
            ", dateAdded='" + getDateAdded() + "'" +
            "}";
    }
}
