package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * Join between {@link Profile} and {@link Interest}.
 */
@Entity
@Table(
    name = "profile_interest",
    uniqueConstraints = { @UniqueConstraint(name = "ux_profile_interest__profile_interest", columnNames = { "profile_id", "interest_id" }) }
)
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProfileInterest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "profileInterests" }, allowSetters = true)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "profileInterests" }, allowSetters = true)
    private Interest interest;

    public ProfileInterest id(Long id) {
        this.setId(id);
        return this;
    }

    public ProfileInterest profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public ProfileInterest interest(Interest interest) {
        this.setInterest(interest);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileInterest)) {
            return false;
        }
        return getId() != null && getId().equals(((ProfileInterest) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ProfileInterest{" + "id=" + getId() + "}";
    }
}
