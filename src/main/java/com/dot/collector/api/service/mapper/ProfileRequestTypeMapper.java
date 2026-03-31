package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.ProfileRequestType;
import com.dot.collector.api.service.dto.ProfileRequestTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfileRequestType} and its DTO {@link ProfileRequestTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfileRequestTypeMapper extends EntityMapper<ProfileRequestTypeDTO, ProfileRequestType> {}
