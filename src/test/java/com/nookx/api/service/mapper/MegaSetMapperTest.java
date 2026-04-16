package com.nookx.api.service.mapper;

import static com.nookx.api.domain.MegaSetAsserts.*;
import static com.nookx.api.domain.MegaSetTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaSetMapperTest {

    private MegaSetMapper megaSetMapper;

    @BeforeEach
    void setUp() {
        megaSetMapper = new MegaSetMapperImpl(new InterestMapperImpl());
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaSetSample1();
        var actual = megaSetMapper.toEntity(megaSetMapper.toDto(expected));
        assertMegaSetAllPropertiesEquals(expected, actual);
    }
}
