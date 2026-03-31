package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.MegaPartSubPartCountAsserts.*;
import static com.dot.collector.api.domain.MegaPartSubPartCountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaPartSubPartCountMapperTest {

    private MegaPartSubPartCountMapper megaPartSubPartCountMapper;

    @BeforeEach
    void setUp() {
        megaPartSubPartCountMapper = new MegaPartSubPartCountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaPartSubPartCountSample1();
        var actual = megaPartSubPartCountMapper.toEntity(megaPartSubPartCountMapper.toDto(expected));
        assertMegaPartSubPartCountAllPropertiesEquals(expected, actual);
    }
}
