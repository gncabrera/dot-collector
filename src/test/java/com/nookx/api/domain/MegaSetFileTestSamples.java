package com.nookx.api.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MegaSetFileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MegaSetFile getMegaSetFileSample1() {
        return new MegaSetFile().id(1L);
    }

    public static MegaSetFile getMegaSetFileSample2() {
        return new MegaSetFile().id(2L);
    }

    public static MegaSetFile getMegaSetFileRandomSampleGenerator() {
        return new MegaSetFile().id(longCount.incrementAndGet());
    }
}
