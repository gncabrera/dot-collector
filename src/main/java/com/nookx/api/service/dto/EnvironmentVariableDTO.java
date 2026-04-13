package com.nookx.api.service.dto;

import com.nookx.api.domain.EnvironmentVariable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO for the {@link EnvironmentVariable} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
public class EnvironmentVariableDTO implements Serializable {

    private Long id;

    private String key;

    private String value;

    private String description;

    private String type;

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
