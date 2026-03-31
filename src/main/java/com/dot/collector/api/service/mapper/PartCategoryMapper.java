package com.dot.collector.api.service.mapper;

import com.dot.collector.api.domain.PartCategory;
import com.dot.collector.api.service.dto.PartCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PartCategory} and its DTO {@link PartCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartCategoryMapper extends EntityMapper<PartCategoryDTO, PartCategory> {}
