package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileRequestTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ProfileRequestType getProfileRequestTypeSample1() {
        return new ProfileRequestType().id(1L).key("key1").name("name1").description("description1");
    }

    public static ProfileRequestType getProfileRequestTypeSample2() {
        return new ProfileRequestType().id(2L).key("key2").name("name2").description("description2");
    }

    public static ProfileRequestType getProfileRequestTypeRandomSampleGenerator() {
        return new ProfileRequestType()
            .id(longCount.incrementAndGet())
            .key(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
