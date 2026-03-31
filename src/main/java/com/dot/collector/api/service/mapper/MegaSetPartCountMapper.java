package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.MegaPart;
import com.dot.collector.api.domain.MegaSet;
import com.dot.collector.api.domain.MegaSetPartCount;
import com.dot.collector.api.service.dto.MegaPartDTO;
import com.dot.collector.api.service.dto.MegaSetDTO;
import com.dot.collector.api.service.dto.MegaSetPartCountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaSetPartCount} and its DTO {@link MegaSetPartCountDTO}.
 */
@Mapper(componentModel = "spring")
public interface MegaSetPartCountMapper extends EntityMapper<MegaSetPartCountDTO, MegaSetPartCount> {
    @Mapping(target = "set", source = "set", qualifiedByName = "megaSetId")
    @Mapping(target = "part", source = "part", qualifiedByName = "megaPartId")
    MegaSetPartCountDTO toDto(MegaSetPartCount s);

    @Named("megaSetId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaSetDTO toDtoMegaSetId(MegaSet megaSet);

    @Named("megaPartId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaPartDTO toDtoMegaPartId(MegaPart megaPart);
}
