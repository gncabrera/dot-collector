package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.MegaPartTypeAsserts.*;
import static com.dot.collector.api.domain.MegaPartTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaPartTypeMapperTest {

    private MegaPartTypeMapper megaPartTypeMapper;

    @BeforeEach
    void setUp() {
        megaPartTypeMapper = new MegaPartTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaPartTypeSample1();
        var actual = megaPartTypeMapper.toEntity(megaPartTypeMapper.toDto(expected));
        assertMegaPartTypeAllPropertiesEquals(expected, actual);
    }
}
