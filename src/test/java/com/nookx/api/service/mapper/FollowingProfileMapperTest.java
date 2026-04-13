package com.nookx.api.service.mapper;

import static com.nookx.api.domain.FollowingProfileAsserts.*;
import static com.nookx.api.domain.FollowingProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FollowingProfileMapperTest {

    private FollowingProfileMapper followingProfileMapper;

    @BeforeEach
    void setUp() {
        followingProfileMapper = new FollowingProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFollowingProfileSample1();
        var actual = followingProfileMapper.toEntity(followingProfileMapper.toDto(expected));
        assertFollowingProfileAllPropertiesEquals(expected, actual);
    }
}
