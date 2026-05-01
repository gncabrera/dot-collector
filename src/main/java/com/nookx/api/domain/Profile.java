package com.nookx.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * A Profile.
 */
@Entity
@Table(name = "profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class Profile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "location")
    private String location;

    @Column(name = "email")
    private String email;

    @Column(name = "instagram")
    private String instagram;

    @Column(name = "facebook")
    private String facebook;

    @Column(name = "whatsapp")
    private String whatsapp;

    @NotNull
    @Column(name = "public_profile", nullable = false)
    private Boolean publicProfile = Boolean.TRUE;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private ProfileImage image;

    public Profile id(Long id) {
        this.setId(id);
        return this;
    }

    public Profile username(String username) {
        this.setUsername(username);
        return this;
    }

    public Profile fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public Profile location(String location) {
        this.setLocation(location);
        return this;
    }

    public Profile email(String email) {
        this.setEmail(email);
        return this;
    }

    public Profile instagram(String instagram) {
        this.setInstagram(instagram);
        return this;
    }

    public Profile facebook(String facebook) {
        this.setFacebook(facebook);
        return this;
    }

    public Profile whatsapp(String whatsapp) {
        this.setWhatsapp(whatsapp);
        return this;
    }

    public Profile publicProfile(Boolean publicProfile) {
        this.setPublicProfile(publicProfile);
        return this;
    }

    public Profile user(User user) {
        this.setUser(user);
        return this;
    }

    public void setImage(ProfileImage image) {
        if (this.image != null) {
            this.image.setProfile(null);
        }
        this.image = image;
        if (image != null) {
            image.setProfile(this);
        }
    }

    public Profile image(ProfileImage image) {
        setImage(image);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        return getId() != null && getId().equals(((Profile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Profile{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", location='" + getLocation() + "'" +
            ", email='" + getEmail() + "'" +
            ", instagram='" + getInstagram() + "'" +
            ", facebook='" + getFacebook() + "'" +
            ", whatsapp='" + getWhatsapp() + "'" +
            ", publicProfile='" + getPublicProfile() + "'" +
            "}";
    }
}
