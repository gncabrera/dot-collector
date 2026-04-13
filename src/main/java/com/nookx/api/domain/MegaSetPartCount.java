package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * A MegaSetPartCount.
 */
@Entity
@Table(name = "mega_set_part_count")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaSetPartCount implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "count")
    private Integer count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "type", "profileCollectionSets" }, allowSetters = true)
    private MegaSet set;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "type", "partCategory", "partSubCategories" }, allowSetters = true)
    private MegaPart part;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public MegaSetPartCount id(Long id) {
        this.setId(id);
        return this;
    }

    public MegaSetPartCount count(Integer count) {
        this.setCount(count);
        return this;
    }

    public MegaSetPartCount set(MegaSet megaSet) {
        this.setSet(megaSet);
        return this;
    }

    public MegaSetPartCount part(MegaPart megaPart) {
        this.setPart(megaPart);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaSetPartCount)) {
            return false;
        }
        return getId() != null && getId().equals(((MegaSetPartCount) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaSetPartCount{" +
            "id=" + getId() +
            ", count=" + getCount() +
            "}";
    }
}
