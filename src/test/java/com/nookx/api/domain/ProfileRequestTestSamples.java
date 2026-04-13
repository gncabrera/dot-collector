package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ProfileRequest getProfileRequestSample1() {
        return new ProfileRequest().id(1L).message("message1");
    }

    public static ProfileRequest getProfileRequestSample2() {
        return new ProfileRequest().id(2L).message("message2");
    }

    public static ProfileRequest getProfileRequestRandomSampleGenerator() {
        return new ProfileRequest().id(longCount.incrementAndGet()).message(UUID.randomUUID().toString());
    }
}
