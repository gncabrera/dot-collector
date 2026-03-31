package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.MegaAttributeAsserts.*;
import static com.dot.collector.api.domain.MegaAttributeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaAttributeMapperTest {

    private MegaAttributeMapper megaAttributeMapper;

    @BeforeEach
    void setUp() {
        megaAttributeMapper = new MegaAttributeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaAttributeSample1();
        var actual = megaAttributeMapper.toEntity(megaAttributeMapper.toDto(expected));
        assertMegaAttributeAllPropertiesEquals(expected, actual);
    }
}
