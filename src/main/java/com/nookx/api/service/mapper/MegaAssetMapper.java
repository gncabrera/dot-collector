package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.service.dto.MegaAssetDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link MegaAsset} and its DTO {@link MegaAssetDTO}.
 */
@Mapper(componentModel = "spring")
public interface MegaAssetMapper extends EntityMapper<MegaAssetDTO, MegaAsset> {}
