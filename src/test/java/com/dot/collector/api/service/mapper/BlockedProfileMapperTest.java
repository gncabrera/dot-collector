package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.BlockedProfileAsserts.*;
import static com.dot.collector.api.domain.BlockedProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlockedProfileMapperTest {

    private BlockedProfileMapper blockedProfileMapper;

    @BeforeEach
    void setUp() {
        blockedProfileMapper = new BlockedProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBlockedProfileSample1();
        var actual = blockedProfileMapper.toEntity(blockedProfileMapper.toDto(expected));
        assertBlockedProfileAllPropertiesEquals(expected, actual);
    }
}
