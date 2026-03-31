package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.MegaPart;
import com.dot.collector.api.domain.MegaPartSubPartCount;
import com.dot.collector.api.service.dto.MegaPartDTO;
import com.dot.collector.api.service.dto.MegaPartSubPartCountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaPartSubPartCount} and its DTO {@link MegaPartSubPartCountDTO}.
 */
@Mapper(componentModel = "spring")
public interface MegaPartSubPartCountMapper extends EntityMapper<MegaPartSubPartCountDTO, MegaPartSubPartCount> {
    @Mapping(target = "part", source = "part", qualifiedByName = "megaPartId")
    @Mapping(target = "parentPart", source = "parentPart", qualifiedByName = "megaPartId")
    MegaPartSubPartCountDTO toDto(MegaPartSubPartCount s);

    @Named("megaPartId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaPartDTO toDtoMegaPartId(MegaPart megaPart);
}
