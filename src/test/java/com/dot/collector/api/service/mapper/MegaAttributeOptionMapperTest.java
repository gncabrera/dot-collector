package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.MegaAttributeOptionAsserts.*;
import static com.dot.collector.api.domain.MegaAttributeOptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaAttributeOptionMapperTest {

    private MegaAttributeOptionMapper megaAttributeOptionMapper;

    @BeforeEach
    void setUp() {
        megaAttributeOptionMapper = new MegaAttributeOptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaAttributeOptionSample1();
        var actual = megaAttributeOptionMapper.toEntity(megaAttributeOptionMapper.toDto(expected));
        assertMegaAttributeOptionAllPropertiesEquals(expected, actual);
    }
}
