package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.Profile;
import com.dot.collector.api.domain.User;
import com.dot.collector.api.service.dto.ProfileDTO;
import com.dot.collector.api.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Profile} and its DTO {@link ProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfileMapper extends EntityMapper<ProfileDTO, Profile> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    ProfileDTO toDto(Profile s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
