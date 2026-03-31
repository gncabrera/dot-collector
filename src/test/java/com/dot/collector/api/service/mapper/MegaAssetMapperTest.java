package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.MegaAssetAsserts.*;
import static com.dot.collector.api.domain.MegaAssetTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MegaAssetMapperTest {

    private MegaAssetMapper megaAssetMapper;

    @BeforeEach
    void setUp() {
        megaAssetMapper = new MegaAssetMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMegaAssetSample1();
        var actual = megaAssetMapper.toEntity(megaAssetMapper.toDto(expected));
        assertMegaAssetAllPropertiesEquals(expected, actual);
    }
}
