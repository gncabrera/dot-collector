package com.dot.collector.api.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.dot.collector.api.domain.BlockedProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlockedProfileDTO implements Serializable {

    private Long id;

    private String reason;

    private LocalDate dateBlocked;

    private ProfileDTO profile;

    private ProfileDTO blockedProfile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getDateBlocked() {
        return dateBlocked;
    }

    public void setDateBlocked(LocalDate dateBlocked) {
        this.dateBlocked = dateBlocked;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }

    public ProfileDTO getBlockedProfile() {
        return blockedProfile;
    }

    public void setBlockedProfile(ProfileDTO blockedProfile) {
        this.blockedProfile = blockedProfile;
    }

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
