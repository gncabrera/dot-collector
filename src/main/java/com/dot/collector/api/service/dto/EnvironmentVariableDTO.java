package com.dot.collector.api.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.dot.collector.api.domain.EnvironmentVariable} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnvironmentVariableDTO implements Serializable {

    private Long id;

    private String key;

    private String value;

    private String description;

    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvironmentVariableDTO)) {
            return false;
        }

        EnvironmentVariableDTO environmentVariableDTO = (EnvironmentVariableDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, environmentVariableDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnvironmentVariableDTO{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", value='" + getValue() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
