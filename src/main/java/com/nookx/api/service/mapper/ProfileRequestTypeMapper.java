package com.nookx.api.service.mapper;

import com.nookx.api.domain.ProfileRequestType;
import com.nookx.api.service.dto.ProfileRequestTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfileRequestType} and its DTO {@link ProfileRequestTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfileRequestTypeMapper extends EntityMapper<ProfileRequestTypeDTO, ProfileRequestType> {}
