package com.dot.collector.api.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link com.dot.collector.api.domain.BlockedProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class BlockedProfileDTO implements Serializable {

    private Long id;

    private String reason;

    private LocalDate dateBlocked;

    private ProfileDTO profile;

    private ProfileDTO blockedProfile;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockedProfileDTO)) {
            return false;
        }

        BlockedProfileDTO blockedProfileDTO = (BlockedProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, blockedProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlockedProfileDTO{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", dateBlocked='" + getDateBlocked() + "'" +
            ", profile=" + getProfile() +
            ", blockedProfile=" + getBlockedProfile() +
            "}";
    }
}
