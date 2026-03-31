package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.ProfileRequestAsserts.*;
import static com.dot.collector.api.domain.ProfileRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileRequestMapperTest {

    private ProfileRequestMapper profileRequestMapper;

    @BeforeEach
    void setUp() {
        profileRequestMapper = new ProfileRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProfileRequestSample1();
        var actual = profileRequestMapper.toEntity(profileRequestMapper.toDto(expected));
        assertProfileRequestAllPropertiesEquals(expected, actual);
    }
}
