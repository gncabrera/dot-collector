package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.service.dto.MegaAssetDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for the entity {@link MegaAsset} and its DTO {@link MegaAssetDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface MegaAssetMapper extends EntityMapper<MegaAssetDTO, MegaAsset> {
    @Override
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "userFromId")
    MegaAsset toEntity(MegaAssetDTO dto);

    @Override
    @Mapping(target = "ownerId", source = "owner.id")
    MegaAssetDTO toDto(MegaAsset entity);

    @Override
    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "userFromId")
    void partialUpdate(@MappingTarget MegaAsset entity, MegaAssetDTO dto);

    @Named("megaAssetFromDtoId")
    default MegaAsset megaAssetFromDtoId(MegaAssetDTO dto) {
        if (dto == null) {
            return null;
        }
        MegaAsset megaAsset = new MegaAsset();
        megaAsset.setId(dto.getId());
        return megaAsset;
    }
}
