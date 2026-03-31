package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.FollowingProfile;
import com.dot.collector.api.domain.Profile;
import com.dot.collector.api.service.dto.FollowingProfileDTO;
import com.dot.collector.api.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FollowingProfile} and its DTO {@link FollowingProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface FollowingProfileMapper extends EntityMapper<FollowingProfileDTO, FollowingProfile> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileId")
    @Mapping(target = "followedProfile", source = "followedProfile", qualifiedByName = "profileId")
    FollowingProfileDTO toDto(FollowingProfile s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
