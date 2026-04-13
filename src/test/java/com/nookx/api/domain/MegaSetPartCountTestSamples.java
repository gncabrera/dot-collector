package com.nookx.api.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MegaSetPartCountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MegaSetPartCount getMegaSetPartCountSample1() {
        return new MegaSetPartCount().id(1L).count(1);
    }

    public static MegaSetPartCount getMegaSetPartCountSample2() {
        return new MegaSetPartCount().id(2L).count(2);
    }

    public static MegaSetPartCount getMegaSetPartCountRandomSampleGenerator() {
        return new MegaSetPartCount().id(longCount.incrementAndGet()).count(intCount.incrementAndGet());
    }
}
