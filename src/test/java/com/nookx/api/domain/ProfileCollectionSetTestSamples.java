package com.nookx.api.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileCollectionSetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ProfileCollectionSet getProfileCollectionSetSample1() {
        return new ProfileCollectionSet().id(1L);
    }

    public static ProfileCollectionSet getProfileCollectionSetSample2() {
        return new ProfileCollectionSet().id(2L);
    }

    public static ProfileCollectionSet getProfileCollectionSetRandomSampleGenerator() {
        return new ProfileCollectionSet().id(longCount.incrementAndGet());
    }
}
