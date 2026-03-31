package com.dot.collector.api.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FollowingProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static FollowingProfile getFollowingProfileSample1() {
        return new FollowingProfile().id(1L);
    }

    public static FollowingProfile getFollowingProfileSample2() {
        return new FollowingProfile().id(2L);
    }

    public static FollowingProfile getFollowingProfileRandomSampleGenerator() {
        return new FollowingProfile().id(longCount.incrementAndGet());
    }
}
