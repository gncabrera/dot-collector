package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.MegaSetType;
import com.nookx.api.domain.ProfileCollectionSet;
import com.nookx.api.service.dto.MegaSetDTO;
import com.nookx.api.service.dto.MegaSetTypeDTO;
import com.nookx.api.service.dto.ProfileCollectionSetDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaSet} and its DTO {@link MegaSetDTO}.
 */
@Mapper(componentModel = "spring", uses = InterestMapper.class)
public interface MegaSetMapper extends EntityMapper<MegaSetDTO, MegaSet> {
    @Mapping(target = "type", source = "type", qualifiedByName = "megaSetTypeId")
    @Mapping(target = "interest", source = "interest", qualifiedByName = "interestId")
    MegaSetDTO toDto(MegaSet s);

    @Mapping(target = "profileCollectionSets", ignore = true)
    @Mapping(target = "removeProfileCollectionSet", ignore = true)
    @Mapping(target = "interest", source = "interest", qualifiedByName = "toEntityInterestId")
    MegaSet toEntity(MegaSetDTO megaSetDTO);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "profileCollectionSets", ignore = true)
    @Mapping(target = "removeProfileCollectionSet", ignore = true)
    @Mapping(target = "interest", source = "interest", qualifiedByName = "toEntityInterestId")
    void partialUpdate(@MappingTarget MegaSet entity, MegaSetDTO dto);

    @Named("megaSetTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaSetTypeDTO toDtoMegaSetTypeId(MegaSetType megaSetType);

    @Named("profileCollectionSetId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileCollectionSetDTO toDtoProfileCollectionSetId(ProfileCollectionSet profileCollectionSet);

    @Named("profileCollectionSetIdSet")
    default Set<ProfileCollectionSetDTO> toDtoProfileCollectionSetIdSet(Set<ProfileCollectionSet> profileCollectionSet) {
        return profileCollectionSet.stream().map(this::toDtoProfileCollectionSetId).collect(Collectors.toSet());
    }
}
