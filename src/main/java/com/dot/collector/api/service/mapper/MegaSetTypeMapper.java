package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.MegaAttribute;
import com.dot.collector.api.domain.MegaSetType;
import com.dot.collector.api.service.dto.MegaAttributeDTO;
import com.dot.collector.api.service.dto.MegaSetTypeDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaSetType} and its DTO {@link MegaSetTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface MegaSetTypeMapper extends EntityMapper<MegaSetTypeDTO, MegaSetType> {
    @Mapping(target = "attributes", source = "attributes", qualifiedByName = "megaAttributeIdSet")
    MegaSetTypeDTO toDto(MegaSetType s);

    @Mapping(target = "removeAttribute", ignore = true)
    MegaSetType toEntity(MegaSetTypeDTO megaSetTypeDTO);

    @Named("megaAttributeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaAttributeDTO toDtoMegaAttributeId(MegaAttribute megaAttribute);

    @Named("megaAttributeIdSet")
    default Set<MegaAttributeDTO> toDtoMegaAttributeIdSet(Set<MegaAttribute> megaAttribute) {
        return megaAttribute.stream().map(this::toDtoMegaAttributeId).collect(Collectors.toSet());
    }
}
