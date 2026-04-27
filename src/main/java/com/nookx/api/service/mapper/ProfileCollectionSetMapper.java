package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.ProfileCollection;
import com.nookx.api.domain.ProfileCollectionSet;
import com.nookx.api.service.dto.MegaSetDTO;
import com.nookx.api.service.dto.ProfileCollectionDTO;
import com.nookx.api.service.dto.ProfileCollectionSetDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfileCollectionSet} and its DTO {@link ProfileCollectionSetDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfileCollectionSetMapper extends EntityMapper<ProfileCollectionSetDTO, ProfileCollectionSet> {
    @Mapping(target = "collection", source = "collection", qualifiedByName = "profileCollectionId")
    @Mapping(target = "set", source = "set", qualifiedByName = "megaSetId")
    ProfileCollectionSetDTO toDto(ProfileCollectionSet s);

    @Named("profileCollectionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileCollectionDTO toDtoProfileCollectionId(ProfileCollection profileCollection);

    @Named("megaSetId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaSetDTO toDtoMegaSetId(MegaSet megaSet);
}
