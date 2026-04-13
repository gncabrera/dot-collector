package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaPart;
import com.nookx.api.domain.PartSubCategory;
import com.nookx.api.service.dto.MegaPartDTO;
import com.nookx.api.service.dto.PartSubCategoryDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PartSubCategory} and its DTO {@link PartSubCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartSubCategoryMapper extends EntityMapper<PartSubCategoryDTO, PartSubCategory> {
    @Mapping(target = "megaParts", source = "megaParts", qualifiedByName = "megaPartIdSet")
    PartSubCategoryDTO toDto(PartSubCategory s);

    @Mapping(target = "megaParts", ignore = true)
    @Mapping(target = "removeMegaPart", ignore = true)
    PartSubCategory toEntity(PartSubCategoryDTO partSubCategoryDTO);

    @Named("megaPartId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaPartDTO toDtoMegaPartId(MegaPart megaPart);

    @Named("megaPartIdSet")
    default Set<MegaPartDTO> toDtoMegaPartIdSet(Set<MegaPart> megaPart) {
        return megaPart.stream().map(this::toDtoMegaPartId).collect(Collectors.toSet());
    }
}
