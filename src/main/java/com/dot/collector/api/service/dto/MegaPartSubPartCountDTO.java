package com.dot.collector.api.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaPartSubPartCount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MegaPartSubPartCountDTO implements Serializable {

    private Long id;

    private Integer count;

    private MegaPartDTO part;

    private MegaPartDTO parentPart;

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

    public MegaPartDTO getPart() {
        return part;
    }

    public void setPart(MegaPartDTO part) {
        this.part = part;
    }

    public MegaPartDTO getParentPart() {
        return parentPart;
    }

    public void setParentPart(MegaPartDTO parentPart) {
        this.parentPart = parentPart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaPartSubPartCountDTO)) {
            return false;
        }

        MegaPartSubPartCountDTO megaPartSubPartCountDTO = (MegaPartSubPartCountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaPartSubPartCountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaPartSubPartCountDTO{" +
            "id=" + getId() +
            ", count=" + getCount() +
            ", part=" + getPart() +
            ", parentPart=" + getParentPart() +
            "}";
    }
}
