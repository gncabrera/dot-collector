package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.MegaSetType;
import com.nookx.api.service.dto.MegaSetDTO;
import com.nookx.api.service.dto.MegaSetTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaSet} and its DTO {@link MegaSetDTO}.
 */
@Mapper(componentModel = "spring", uses = { InterestMapper.class, UserMapper.class })
public interface MegaSetMapper extends EntityMapper<MegaSetDTO, MegaSet> {
    @Mapping(target = "type", source = "type", qualifiedByName = "megaSetTypeId")
    @Mapping(target = "interest", source = "interest", qualifiedByName = "interestId")
    @Mapping(target = "ownerId", source = "owner.id")
    MegaSetDTO toDto(MegaSet s);

    @Mapping(target = "interest", source = "interest", qualifiedByName = "toEntityInterestId")
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "userFromId")
    MegaSet toEntity(MegaSetDTO megaSetDTO);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "interest", source = "interest", qualifiedByName = "toEntityInterestId")
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "userFromId")
    void partialUpdate(@MappingTarget MegaSet entity, MegaSetDTO dto);

    @Named("megaSetTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaSetTypeDTO toDtoMegaSetTypeId(MegaSetType megaSetType);
}
