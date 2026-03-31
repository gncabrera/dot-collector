package com.dot.collector.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BlockedProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static BlockedProfile getBlockedProfileSample1() {
        return new BlockedProfile().id(1L).reason("reason1");
    }

    public static BlockedProfile getBlockedProfileSample2() {
        return new BlockedProfile().id(2L).reason("reason2");
    }

    public static BlockedProfile getBlockedProfileRandomSampleGenerator() {
        return new BlockedProfile().id(longCount.incrementAndGet()).reason(UUID.randomUUID().toString());
    }
}
