package com.nookx.api.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * A ProfileRequestType.
 */
@Entity
@Table(name = "profile_request_type")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProfileRequestType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "key", unique = true)
    private String key;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ProfileRequestType id(Long id) {
        this.setId(id);
        return this;
    }

    public ProfileRequestType key(String key) {
        this.setKey(key);
        return this;
    }

    public ProfileRequestType name(String name) {
        this.setName(name);
        return this;
    }

    public ProfileRequestType description(String description) {
        this.setDescription(description);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileRequestType)) {
            return false;
        }
        return getId() != null && getId().equals(((ProfileRequestType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileRequestType{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
