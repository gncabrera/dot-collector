package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EnvironmentVariableTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static EnvironmentVariable getEnvironmentVariableSample1() {
        return new EnvironmentVariable().id(1L).key("key1").value("value1").description("description1").type("type1");
    }

    public static EnvironmentVariable getEnvironmentVariableSample2() {
        return new EnvironmentVariable().id(2L).key("key2").value("value2").description("description2").type("type2");
    }

    public static EnvironmentVariable getEnvironmentVariableRandomSampleGenerator() {
        return new EnvironmentVariable()
            .id(longCount.incrementAndGet())
            .key(UUID.randomUUID().toString())
            .value(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .type(UUID.randomUUID().toString());
    }
}
