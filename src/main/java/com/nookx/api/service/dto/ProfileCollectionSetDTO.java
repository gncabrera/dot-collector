package com.nookx.api.service.dto;

import com.nookx.api.domain.ProfileCollectionSet;
import com.nookx.api.domain.enumeration.ProfileCollectionSetStatus;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link ProfileCollectionSet} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class ProfileCollectionSetDTO implements Serializable {

    private Long id;

    private Boolean owned;

    private LocalDate dateAdded;

    private String userNotes;

    private Float price;

    private Integer quantityToSell;

    private ProfileCollectionSetStatus status;

    private ProfileCollectionDTO collection;

    private MegaSetDTO set;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileCollectionSetDTO)) {
            return false;
        }

        ProfileCollectionSetDTO profileCollectionSetDTO = (ProfileCollectionSetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, profileCollectionSetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileCollectionSetDTO{" +
            "id=" + getId() +
            ", owned='" + getOwned() + "'" +
            ", dateAdded='" + getDateAdded() + "'" +
            ", userNotes='" + getUserNotes() + "'" +
            ", price=" + getPrice() +
            ", quantityToSell=" + getQuantityToSell() +
            ", status='" + getStatus() + "'" +
            ", collection=" + getCollection() +
            ", set=" + getSet() +
            "}";
    }
}
