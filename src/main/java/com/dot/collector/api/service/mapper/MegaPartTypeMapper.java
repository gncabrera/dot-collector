package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.MegaAttribute;
import com.dot.collector.api.domain.MegaPartType;
import com.dot.collector.api.service.dto.MegaAttributeDTO;
import com.dot.collector.api.service.dto.MegaPartTypeDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaPartType} and its DTO {@link MegaPartTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface MegaPartTypeMapper extends EntityMapper<MegaPartTypeDTO, MegaPartType> {
    @Mapping(target = "attributes", source = "attributes", qualifiedByName = "megaAttributeIdSet")
    MegaPartTypeDTO toDto(MegaPartType s);

    @Mapping(target = "removeAttribute", ignore = true)
    MegaPartType toEntity(MegaPartTypeDTO megaPartTypeDTO);

    @Named("megaAttributeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaAttributeDTO toDtoMegaAttributeId(MegaAttribute megaAttribute);

    @Named("megaAttributeIdSet")
    default Set<MegaAttributeDTO> toDtoMegaAttributeIdSet(Set<MegaAttribute> megaAttribute) {
        return megaAttribute.stream().map(this::toDtoMegaAttributeId).collect(Collectors.toSet());
    }
}
