package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.ProfileCollectionAsserts.*;
import static com.dot.collector.api.domain.ProfileCollectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileCollectionMapperTest {

    private ProfileCollectionMapper profileCollectionMapper;

    @BeforeEach
    void setUp() {
        profileCollectionMapper = new ProfileCollectionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProfileCollectionSample1();
        var actual = profileCollectionMapper.toEntity(profileCollectionMapper.toDto(expected));
        assertProfileCollectionAllPropertiesEquals(expected, actual);
    }
}
