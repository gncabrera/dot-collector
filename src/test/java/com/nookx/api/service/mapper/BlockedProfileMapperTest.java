package com.nookx.api.service.mapper;

import static com.nookx.api.domain.BlockedProfileAsserts.*;
import static com.nookx.api.domain.BlockedProfileTestSamples.*;

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
