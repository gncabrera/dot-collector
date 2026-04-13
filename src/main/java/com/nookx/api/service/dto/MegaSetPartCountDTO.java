package com.nookx.api.service.dto;

import com.nookx.api.domain.MegaSetPartCount;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link MegaSetPartCount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaSetPartCountDTO implements Serializable {

    private Long id;

    private Integer count;

    private MegaSetDTO set;

    private MegaPartDTO part;

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
