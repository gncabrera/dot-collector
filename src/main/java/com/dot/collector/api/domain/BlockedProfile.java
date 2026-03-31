package com.dot.collector.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A BlockedProfile.
 */
@Entity
@Table(name = "blocked_profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Profile blockedProfile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BlockedProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return this.reason;
    }

    public BlockedProfile reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getDateBlocked() {
        return this.dateBlocked;
    }

    public BlockedProfile dateBlocked(LocalDate dateBlocked) {
        this.setDateBlocked(dateBlocked);
        return this;
    }

    public void setDateBlocked(LocalDate dateBlocked) {
        this.dateBlocked = dateBlocked;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public BlockedProfile profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public Profile getBlockedProfile() {
        return this.blockedProfile;
    }

    public void setBlockedProfile(Profile profile) {
        this.blockedProfile = profile;
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
