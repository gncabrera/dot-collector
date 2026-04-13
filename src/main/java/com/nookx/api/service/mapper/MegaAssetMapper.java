package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.MegaPart;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.service.dto.MegaAssetDTO;
import com.nookx.api.service.dto.MegaPartDTO;
import com.nookx.api.service.dto.MegaSetDTO;
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
