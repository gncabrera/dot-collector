package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.MegaSetFile;
import com.nookx.api.service.dto.MegaAssetDTO;
import com.nookx.api.service.dto.MegaSetDTO;
import com.nookx.api.service.dto.MegaSetFileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaSetFile} and its DTO {@link MegaSetFileDTO}.
 */
@Mapper(componentModel = "spring", uses = { MegaAssetMapper.class })
public interface MegaSetFileMapper extends EntityMapper<MegaSetFileDTO, MegaSetFile> {
    @Mapping(target = "megaSet", source = "megaSet", qualifiedByName = "megaSetId")
    @Mapping(target = "asset", source = "asset", qualifiedByName = "megaAssetId")
    MegaSetFileDTO toDto(MegaSetFile entity);

    @Override
    @Mapping(target = "megaSet", source = "megaSet", qualifiedByName = "megaSetFromDtoId")
    @Mapping(target = "asset", source = "asset", qualifiedByName = "megaAssetFromDtoId")
    MegaSetFile toEntity(MegaSetFileDTO dto);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "megaSet", source = "megaSet", qualifiedByName = "megaSetFromDtoId")
    @Mapping(target = "asset", source = "asset", qualifiedByName = "megaAssetFromDtoId")
    void partialUpdate(@MappingTarget MegaSetFile entity, MegaSetFileDTO dto);

    @Named("megaSetId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaSetDTO toDtoMegaSetId(MegaSet megaSet);

    @Named("megaAssetId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaAssetDTO toDtoMegaAssetId(MegaAsset asset);

    @Named("megaSetFromDtoId")
    default MegaSet megaSetFromDtoId(MegaSetDTO dto) {
        if (dto == null) {
            return null;
        }
        MegaSet megaSet = new MegaSet();
        megaSet.setId(dto.getId());
        return megaSet;
    }
}
