package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * A BlockedProfile.
 */
@Entity
@Table(
    name = "blocked_profile",
    uniqueConstraints = @UniqueConstraint(
        name = "ux_blocked_profile__profile_id_blocked_profile_id",
        columnNames = { "profile_id", "blocked_profile_id" }
    )
)
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class BlockedProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "reason")
    private String reason;

    @Column(name = "date_blocked")
    private LocalDate dateBlocked;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Profile profile;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blocked_profile_id", nullable = false)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Profile blockedProfile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public BlockedProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public BlockedProfile reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public BlockedProfile dateBlocked(LocalDate dateBlocked) {
        this.setDateBlocked(dateBlocked);
        return this;
    }

    public BlockedProfile profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public BlockedProfile blockedProfile(Profile profile) {
        this.setBlockedProfile(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockedProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((BlockedProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlockedProfile{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", dateBlocked='" + getDateBlocked() + "'" +
            "}";
    }
}
