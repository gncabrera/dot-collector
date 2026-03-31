package com.dot.collector.api.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaSetPartCount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MegaSetPartCountDTO implements Serializable {

    private Long id;

    private Integer count;

    private MegaSetDTO set;

    private MegaPartDTO part;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public MegaSetDTO getSet() {
        return set;
    }

    public void setSet(MegaSetDTO set) {
        this.set = set;
    }

    public MegaPartDTO getPart() {
        return part;
    }

    public void setPart(MegaPartDTO part) {
        this.part = part;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaSetPartCountDTO)) {
            return false;
        }

        MegaSetPartCountDTO megaSetPartCountDTO = (MegaSetPartCountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaSetPartCountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaSetPartCountDTO{" +
            "id=" + getId() +
            ", count=" + getCount() +
            ", set=" + getSet() +
            ", part=" + getPart() +
            "}";
    }
}
