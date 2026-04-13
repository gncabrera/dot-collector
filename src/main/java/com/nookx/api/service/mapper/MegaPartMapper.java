package com.nookx.api.service.mapper;

import com.nookx.api.domain.MegaPart;
import com.nookx.api.domain.MegaPartType;
import com.nookx.api.domain.PartCategory;
import com.nookx.api.domain.PartSubCategory;
import com.nookx.api.service.dto.MegaPartDTO;
import com.nookx.api.service.dto.MegaPartTypeDTO;
import com.nookx.api.service.dto.PartCategoryDTO;
import com.nookx.api.service.dto.PartSubCategoryDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MegaPart} and its DTO {@link MegaPartDTO}.
 */
@Mapper(componentModel = "spring")
public interface MegaPartMapper extends EntityMapper<MegaPartDTO, MegaPart> {
    @Mapping(target = "type", source = "type", qualifiedByName = "megaPartTypeId")
    @Mapping(target = "partCategory", source = "partCategory", qualifiedByName = "partCategoryId")
    @Mapping(target = "partSubCategories", source = "partSubCategories", qualifiedByName = "partSubCategoryIdSet")
    MegaPartDTO toDto(MegaPart s);

    @Mapping(target = "removePartSubCategory", ignore = true)
    MegaPart toEntity(MegaPartDTO megaPartDTO);

    @Named("megaPartTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MegaPartTypeDTO toDtoMegaPartTypeId(MegaPartType megaPartType);

    @Named("partCategoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PartCategoryDTO toDtoPartCategoryId(PartCategory partCategory);

    @Named("partSubCategoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PartSubCategoryDTO toDtoPartSubCategoryId(PartSubCategory partSubCategory);

    @Named("partSubCategoryIdSet")
    default Set<PartSubCategoryDTO> toDtoPartSubCategoryIdSet(Set<PartSubCategory> partSubCategory) {
        return partSubCategory.stream().map(this::toDtoPartSubCategoryId).collect(Collectors.toSet());
    }
}
