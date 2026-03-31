package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.MegaSetAsserts.*;
import static com.dot.collector.api.domain.MegaSetTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaSetMapperTest {

    private MegaSetMapper megaSetMapper;

    @BeforeEach
    void setUp() {
        megaSetMapper = new MegaSetMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaSetSample1();
        var actual = megaSetMapper.toEntity(megaSetMapper.toDto(expected));
        assertMegaSetAllPropertiesEquals(expected, actual);
    }
}
