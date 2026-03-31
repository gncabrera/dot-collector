package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.MegaSetPartCountAsserts.*;
import static com.dot.collector.api.domain.MegaSetPartCountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaSetPartCountMapperTest {

    private MegaSetPartCountMapper megaSetPartCountMapper;

    @BeforeEach
    void setUp() {
        megaSetPartCountMapper = new MegaSetPartCountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaSetPartCountSample1();
        var actual = megaSetPartCountMapper.toEntity(megaSetPartCountMapper.toDto(expected));
        assertMegaSetPartCountAllPropertiesEquals(expected, actual);
    }
}
