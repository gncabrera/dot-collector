package com.dot.collector.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * A MegaPartSubPartCount.
 */
@Entity
@Table(name = "mega_part_sub_part_count")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaPartSubPartCount implements Serializable {

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
    @JsonIgnoreProperties(value = { "type", "partCategory", "partSubCategories" }, allowSetters = true)
    private MegaPart part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "type", "partCategory", "partSubCategories" }, allowSetters = true)
    private MegaPart parentPart;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public MegaPartSubPartCount id(Long id) {
        this.setId(id);
        return this;
    }

    public MegaPartSubPartCount count(Integer count) {
        this.setCount(count);
        return this;
    }

    public MegaPartSubPartCount part(MegaPart megaPart) {
        this.setPart(megaPart);
        return this;
    }

    public MegaPartSubPartCount parentPart(MegaPart megaPart) {
        this.setParentPart(megaPart);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaPartSubPartCount)) {
            return false;
        }
        return getId() != null && getId().equals(((MegaPartSubPartCount) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaPartSubPartCount{" +
            "id=" + getId() +
            ", count=" + getCount() +
            "}";
    }
}
