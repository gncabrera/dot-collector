package com.dot.collector.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * A ProfileRequest.
 */
@Entity
@Table(name = "profile_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProfileRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "message")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileRequestType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Profile profile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ProfileRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public ProfileRequest message(String message) {
        this.setMessage(message);
        return this;
    }

    public ProfileRequest type(ProfileRequestType profileRequestType) {
        this.setType(profileRequestType);
        return this;
    }

    public ProfileRequest profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((ProfileRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileRequest{" +
            "id=" + getId() +
            ", message='" + getMessage() + "'" +
            "}";
    }
}
