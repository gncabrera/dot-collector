package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.MegaAttribute;
import com.dot.collector.api.domain.MegaAttributeOption;
import com.dot.collector.api.service.dto.MegaAttributeDTO;
import com.dot.collector.api.service.dto.MegaAttributeOptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaAttributeOption} and its DTO {@link MegaAttributeOptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface MegaAttributeOptionMapper extends EntityMapper<MegaAttributeOptionDTO, MegaAttributeOption> {
    @Mapping(target = "attribute", source = "attribute", qualifiedByName = "megaAttributeId")
    MegaAttributeOptionDTO toDto(MegaAttributeOption s);

    @Named("megaAttributeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaAttributeDTO toDtoMegaAttributeId(MegaAttribute megaAttribute);
}
