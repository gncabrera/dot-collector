package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.Profile;
import com.dot.collector.api.domain.ProfileCollection;
import com.dot.collector.api.service.dto.ProfileCollectionDTO;
import com.dot.collector.api.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfileCollection} and its DTO {@link ProfileCollectionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfileCollectionMapper extends EntityMapper<ProfileCollectionDTO, ProfileCollection> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileId")
    ProfileCollectionDTO toDto(ProfileCollection s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
