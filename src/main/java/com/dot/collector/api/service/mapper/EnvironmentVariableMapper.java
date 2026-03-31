package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.EnvironmentVariable;
import com.dot.collector.api.service.dto.EnvironmentVariableDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EnvironmentVariable} and its DTO {@link EnvironmentVariableDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnvironmentVariableMapper extends EntityMapper<EnvironmentVariableDTO, EnvironmentVariable> {}
