package com.nookx.api.service.dto;

import com.nookx.api.domain.MegaSetFile;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link MegaSetFile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class MegaSetFileDTO implements Serializable {

    private Long id;

    private MegaSetDTO megaSet;

    private MegaAssetDTO asset;

    private Integer sortOrder;

    private String label;

    private Boolean isPrimary;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MegaSetFileDTO)) {
            return false;
        }

        MegaSetFileDTO megaSetFileDTO = (MegaSetFileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaSetFileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaSetFileDTO{" +
            "id=" + getId() +
            ", megaSet=" + getMegaSet() +
            ", asset=" + getAsset() +
            ", sortOrder=" + getSortOrder() +
            ", label='" + getLabel() + "'" +
            ", isPrimary=" + getIsPrimary() +
            "}";
    }
}
