package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.MegaAsset;
import com.dot.collector.api.domain.MegaPart;
import com.dot.collector.api.domain.MegaSet;
import com.dot.collector.api.service.dto.MegaAssetDTO;
import com.dot.collector.api.service.dto.MegaPartDTO;
import com.dot.collector.api.service.dto.MegaSetDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaAsset} and its DTO {@link MegaAssetDTO}.
 */
@Mapper(componentModel = "spring")
public interface MegaAssetMapper extends EntityMapper<MegaAssetDTO, MegaAsset> {
    @Mapping(target = "set", source = "set", qualifiedByName = "megaSetId")
    @Mapping(target = "part", source = "part", qualifiedByName = "megaPartId")
    MegaAssetDTO toDto(MegaAsset s);

    @Named("megaSetId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaSetDTO toDtoMegaSetId(MegaSet megaSet);

    @Named("megaPartId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaPartDTO toDtoMegaPartId(MegaPart megaPart);
}
