package com.nookx.api.service.mapper;

import com.nookx.api.domain.PartCategory;
import com.nookx.api.service.dto.PartCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PartCategory} and its DTO {@link PartCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartCategoryMapper extends EntityMapper<PartCategoryDTO, PartCategory> {}
