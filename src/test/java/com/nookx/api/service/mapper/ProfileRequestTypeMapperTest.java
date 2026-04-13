package com.nookx.api.service.mapper;

import static com.nookx.api.domain.ProfileRequestTypeAsserts.*;
import static com.nookx.api.domain.ProfileRequestTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileRequestTypeMapperTest {

    private ProfileRequestTypeMapper profileRequestTypeMapper;

    @BeforeEach
    void setUp() {
        profileRequestTypeMapper = new ProfileRequestTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProfileRequestTypeSample1();
        var actual = profileRequestTypeMapper.toEntity(profileRequestTypeMapper.toDto(expected));
        assertProfileRequestTypeAllPropertiesEquals(expected, actual);
    }
}
