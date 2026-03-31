package com.dot.collector.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Profile getProfileSample1() {
        return new Profile().id(1L).username("username1").fullName("fullName1");
    }

    public static Profile getProfileSample2() {
        return new Profile().id(2L).username("username2").fullName("fullName2");
    }

    public static Profile getProfileRandomSampleGenerator() {
        return new Profile().id(longCount.incrementAndGet()).username(UUID.randomUUID().toString()).fullName(UUID.randomUUID().toString());
    }
}
