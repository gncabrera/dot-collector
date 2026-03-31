package com.dot.collector.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MegaPartTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MegaPartType getMegaPartTypeSample1() {
        return new MegaPartType().id(1L).name("name1").version(1);
    }

    public static MegaPartType getMegaPartTypeSample2() {
        return new MegaPartType().id(2L).name("name2").version(2);
    }

    public static MegaPartType getMegaPartTypeRandomSampleGenerator() {
        return new MegaPartType().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).version(intCount.incrementAndGet());
    }
}
