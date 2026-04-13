package com.nookx.api.service.mapper;

import com.nookx.api.domain.EnvironmentVariable;
import com.nookx.api.service.dto.EnvironmentVariableDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EnvironmentVariable} and its DTO {@link EnvironmentVariableDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnvironmentVariableMapper extends EntityMapper<EnvironmentVariableDTO, EnvironmentVariable> {}
