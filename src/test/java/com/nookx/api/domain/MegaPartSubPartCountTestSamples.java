package com.nookx.api.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MegaPartSubPartCountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MegaPartSubPartCount getMegaPartSubPartCountSample1() {
        return new MegaPartSubPartCount().id(1L).count(1);
    }

    public static MegaPartSubPartCount getMegaPartSubPartCountSample2() {
        return new MegaPartSubPartCount().id(2L).count(2);
    }

    public static MegaPartSubPartCount getMegaPartSubPartCountRandomSampleGenerator() {
        return new MegaPartSubPartCount().id(longCount.incrementAndGet()).count(intCount.incrementAndGet());
    }
}
