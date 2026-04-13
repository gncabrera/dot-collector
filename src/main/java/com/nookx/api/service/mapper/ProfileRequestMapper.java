package com.nookx.api.service.mapper;

import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileRequest;
import com.nookx.api.domain.ProfileRequestType;
import com.nookx.api.service.dto.ProfileDTO;
import com.nookx.api.service.dto.ProfileRequestDTO;
import com.nookx.api.service.dto.ProfileRequestTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfileRequest} and its DTO {@link ProfileRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfileRequestMapper extends EntityMapper<ProfileRequestDTO, ProfileRequest> {
    @Mapping(target = "type", source = "type", qualifiedByName = "profileRequestTypeId")
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileId")
    ProfileRequestDTO toDto(ProfileRequest s);

    @Named("profileRequestTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileRequestTypeDTO toDtoProfileRequestTypeId(ProfileRequestType profileRequestType);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
