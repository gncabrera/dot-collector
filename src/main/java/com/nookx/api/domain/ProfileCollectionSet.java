package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id")
    @JsonIgnoreProperties(value = { "type" }, allowSetters = true)
    private MegaSet set;

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

    public ProfileCollectionSet set(MegaSet megaSet) {
        this.setSet(megaSet);
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
