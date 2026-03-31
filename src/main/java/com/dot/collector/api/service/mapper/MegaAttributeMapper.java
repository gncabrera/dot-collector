package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.MegaAttribute;
import com.dot.collector.api.domain.MegaPartType;
import com.dot.collector.api.domain.MegaSetType;
import com.dot.collector.api.service.dto.MegaAttributeDTO;
import com.dot.collector.api.service.dto.MegaPartTypeDTO;
import com.dot.collector.api.service.dto.MegaSetTypeDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaAttribute} and its DTO {@link MegaAttributeDTO}.
 */
@Mapper(componentModel = "spring")
public interface MegaAttributeMapper extends EntityMapper<MegaAttributeDTO, MegaAttribute> {
    @Mapping(target = "setTypes", source = "setTypes", qualifiedByName = "megaSetTypeIdSet")
    @Mapping(target = "partTypes", source = "partTypes", qualifiedByName = "megaPartTypeIdSet")
    MegaAttributeDTO toDto(MegaAttribute s);

    @Mapping(target = "setTypes", ignore = true)
    @Mapping(target = "removeSetType", ignore = true)
    @Mapping(target = "partTypes", ignore = true)
    @Mapping(target = "removePartType", ignore = true)
    MegaAttribute toEntity(MegaAttributeDTO megaAttributeDTO);

    @Named("megaSetTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaSetTypeDTO toDtoMegaSetTypeId(MegaSetType megaSetType);

    @Named("megaSetTypeIdSet")
    default Set<MegaSetTypeDTO> toDtoMegaSetTypeIdSet(Set<MegaSetType> megaSetType) {
        return megaSetType.stream().map(this::toDtoMegaSetTypeId).collect(Collectors.toSet());
    }

    @Named("megaPartTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaPartTypeDTO toDtoMegaPartTypeId(MegaPartType megaPartType);

    @Named("megaPartTypeIdSet")
    default Set<MegaPartTypeDTO> toDtoMegaPartTypeIdSet(Set<MegaPartType> megaPartType) {
        return megaPartType.stream().map(this::toDtoMegaPartTypeId).collect(Collectors.toSet());
    }
}
