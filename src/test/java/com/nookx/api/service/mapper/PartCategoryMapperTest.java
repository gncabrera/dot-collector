package com.nookx.api.service.mapper;

import static com.nookx.api.domain.PartCategoryAsserts.*;
import static com.nookx.api.domain.PartCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartCategoryMapperTest {

    private PartCategoryMapper partCategoryMapper;

    @BeforeEach
    void setUp() {
        partCategoryMapper = new PartCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPartCategorySample1();
        var actual = partCategoryMapper.toEntity(partCategoryMapper.toDto(expected));
        assertPartCategoryAllPropertiesEquals(expected, actual);
    }
}
