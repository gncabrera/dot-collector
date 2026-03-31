package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.MegaPart;
import com.dot.collector.api.domain.MegaPartType;
import com.dot.collector.api.domain.PartCategory;
import com.dot.collector.api.domain.PartSubCategory;
import com.dot.collector.api.service.dto.MegaPartDTO;
import com.dot.collector.api.service.dto.MegaPartTypeDTO;
import com.dot.collector.api.service.dto.PartCategoryDTO;
import com.dot.collector.api.service.dto.PartSubCategoryDTO;
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
