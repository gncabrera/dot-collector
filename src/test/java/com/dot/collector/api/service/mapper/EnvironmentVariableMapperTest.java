package com.dot.collector.api.service.mapper;

import static com.dot.collector.api.domain.EnvironmentVariableAsserts.*;
import static com.dot.collector.api.domain.EnvironmentVariableTestSamples.*;

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
