package com.nookx.api.service.mapper;

import static com.nookx.api.domain.EnvironmentVariableAsserts.*;
import static com.nookx.api.domain.EnvironmentVariableTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnvironmentVariableMapperTest {

    private EnvironmentVariableMapper environmentVariableMapper;

    @BeforeEach
    void setUp() {
        environmentVariableMapper = new EnvironmentVariableMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEnvironmentVariableSample1();
        var actual = environmentVariableMapper.toEntity(environmentVariableMapper.toDto(expected));
        assertEnvironmentVariableAllPropertiesEquals(expected, actual);
    }
}
