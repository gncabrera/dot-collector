package com.nookx.api.service.mapper;

import static com.nookx.api.domain.ProfileCollectionSetAsserts.*;
import static com.nookx.api.domain.ProfileCollectionSetTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileCollectionSetMapperTest {

    private ProfileCollectionSetMapper profileCollectionSetMapper;

    @BeforeEach
    void setUp() {
        profileCollectionSetMapper = new ProfileCollectionSetMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProfileCollectionSetSample1();
        var actual = profileCollectionSetMapper.toEntity(profileCollectionSetMapper.toDto(expected));
        assertProfileCollectionSetAllPropertiesEquals(expected, actual);
    }
}
