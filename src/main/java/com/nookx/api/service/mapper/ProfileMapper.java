package com.nookx.api.service.mapper;

import com.nookx.api.domain.Profile;
import com.nookx.api.domain.User;
import com.nookx.api.service.dto.ProfileDTO;
import com.nookx.api.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Profile} and its DTO {@link ProfileDTO}.
 */
@Mapper(componentModel = "spring", uses = { InterestMapper.class, ProfileImageMapper.class })
public interface ProfileMapper extends EntityMapper<ProfileDTO, Profile> {
    @Mapping(target = "image", source = "image")
    ProfileDTO toDto(Profile s);

    @Override
    @Mapping(target = "image.profile", ignore = true)
    Profile toEntity(ProfileDTO dto);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "image.profile", ignore = true)
    void partialUpdate(@MappingTarget Profile entity, ProfileDTO dto);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
