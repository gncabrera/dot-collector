package com.dot.collector.api.service.dto;

import com.dot.collector.api.domain.enumeration.AssetType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dot.collector.api.domain.MegaAsset} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MegaAssetDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private String path;

    private AssetType type;

    private MegaSetDTO set;

    private MegaPartDTO part;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
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
        if (!(o instanceof MegaAssetDTO)) {
            return false;
        }

        MegaAssetDTO megaAssetDTO = (MegaAssetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, megaAssetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MegaAssetDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", path='" + getPath() + "'" +
            ", type='" + getType() + "'" +
            ", set=" + getSet() +
            ", part=" + getPart() +
            "}";
    }
}
