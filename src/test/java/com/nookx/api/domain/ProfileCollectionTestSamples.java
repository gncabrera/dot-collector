package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileCollectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ProfileCollection getProfileCollectionSample1() {
        return new ProfileCollection().id(1L).title("title1").description("description1");
    }

    public static ProfileCollection getProfileCollectionSample2() {
        return new ProfileCollection().id(2L).title("title2").description("description2");
    }

    public static ProfileCollection getProfileCollectionRandomSampleGenerator() {
        return new ProfileCollection()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
