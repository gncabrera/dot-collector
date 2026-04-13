package com.nookx.api.service.mapper;

import static com.nookx.api.domain.MegaPartAsserts.*;
import static com.nookx.api.domain.MegaPartTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaPartMapperTest {

    private MegaPartMapper megaPartMapper;

    @BeforeEach
    void setUp() {
        megaPartMapper = new MegaPartMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaPartSample1();
        var actual = megaPartMapper.toEntity(megaPartMapper.toDto(expected));
        assertMegaPartAllPropertiesEquals(expected, actual);
    }
}
