package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MegaSetTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MegaSetType getMegaSetTypeSample1() {
        return new MegaSetType().id(1L).name("name1").version(1);
    }

    public static MegaSetType getMegaSetTypeSample2() {
        return new MegaSetType().id(2L).name("name2").version(2);
    }

    public static MegaSetType getMegaSetTypeRandomSampleGenerator() {
        return new MegaSetType().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).version(intCount.incrementAndGet());
    }
}
