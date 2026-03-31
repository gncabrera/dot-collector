package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.MegaSetTypeAsserts.*;
import static com.dot.collector.api.domain.MegaSetTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaSetTypeMapperTest {

    private MegaSetTypeMapper megaSetTypeMapper;

    @BeforeEach
    void setUp() {
        megaSetTypeMapper = new MegaSetTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaSetTypeSample1();
        var actual = megaSetTypeMapper.toEntity(megaSetTypeMapper.toDto(expected));
        assertMegaSetTypeAllPropertiesEquals(expected, actual);
    }
}
