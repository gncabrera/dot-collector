package com.nookx.api.service.mapper;

import static com.nookx.api.domain.PartSubCategoryAsserts.*;
import static com.nookx.api.domain.PartSubCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartSubCategoryMapperTest {

    private PartSubCategoryMapper partSubCategoryMapper;

    @BeforeEach
    void setUp() {
        partSubCategoryMapper = new PartSubCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPartSubCategorySample1();
        var actual = partSubCategoryMapper.toEntity(partSubCategoryMapper.toDto(expected));
        assertPartSubCategoryAllPropertiesEquals(expected, actual);
    }
}
