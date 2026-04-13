package com.nookx.api.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * A EnvironmentVariable.
 */
@Entity
@Table(name = "environment_variable")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class EnvironmentVariable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "key", unique = true)
    private String key;

    @Column(name = "value")
    private String value;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public EnvironmentVariable id(Long id) {
        this.setId(id);
        return this;
    }

    public EnvironmentVariable key(String key) {
        this.setKey(key);
        return this;
    }

    public EnvironmentVariable value(String value) {
        this.setValue(value);
        return this;
    }

    public EnvironmentVariable description(String description) {
        this.setDescription(description);
        return this;
    }

    public EnvironmentVariable type(String type) {
        this.setType(type);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvironmentVariable)) {
            return false;
        }
        return getId() != null && getId().equals(((EnvironmentVariable) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnvironmentVariable{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
