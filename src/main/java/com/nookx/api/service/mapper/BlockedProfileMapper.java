package com.nookx.api.service.mapper;

import com.nookx.api.domain.BlockedProfile;
import com.nookx.api.domain.Profile;
import com.nookx.api.service.dto.BlockedProfileDTO;
import com.nookx.api.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BlockedProfile} and its DTO {@link BlockedProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface BlockedProfileMapper extends EntityMapper<BlockedProfileDTO, BlockedProfile> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileId")
    @Mapping(target = "blockedProfile", source = "blockedProfile", qualifiedByName = "profileId")
    BlockedProfileDTO toDto(BlockedProfile s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
