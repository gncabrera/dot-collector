package com.nookx.api.service.mapper;

import static com.nookx.api.domain.MegaAttributeOptionAsserts.*;
import static com.nookx.api.domain.MegaAttributeOptionTestSamples.*;

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
