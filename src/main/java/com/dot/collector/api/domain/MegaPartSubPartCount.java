package com.dot.collector.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * A MegaPartSubPartCount.
 */
@Entity
@Table(name = "mega_part_sub_part_count")
@SuppressWarnings("common-java:DuplicatedBlocks")
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

    public Long getId() {
        return this.id;
    }

    public MegaPartSubPartCount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCount() {
        return this.count;
    }

    public MegaPartSubPartCount count(Integer count) {
        this.setCount(count);
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public MegaPart getPart() {
        return this.part;
    }

    public void setPart(MegaPart megaPart) {
        this.part = megaPart;
    }

    public MegaPartSubPartCount part(MegaPart megaPart) {
        this.setPart(megaPart);
        return this;
    }

    public MegaPart getParentPart() {
        return this.parentPart;
    }

    public void setParentPart(MegaPart megaPart) {
        this.parentPart = megaPart;
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
