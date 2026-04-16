package com.nookx.api.service.mapper;

import static com.nookx.api.domain.ProfileCollectionAsserts.*;
import static com.nookx.api.domain.ProfileCollectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileCollectionMapperTest {

    private ProfileCollectionMapper profileCollectionMapper;

    @BeforeEach
    void setUp() {
        profileCollectionMapper = new ProfileCollectionMapperImpl(
            new ProfileCollectionImageMapperImpl(new MegaAssetMapperImpl(new UserMapper())),
            new InterestMapperImpl()
        );
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProfileCollectionSample1();
        var actual = profileCollectionMapper.toEntity(profileCollectionMapper.toDto(expected));
        assertProfileCollectionAllPropertiesEquals(expected, actual);
    }
}
