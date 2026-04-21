package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MegaSetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MegaSet getMegaSetSample1() {
        return new MegaSet().id(1L).setNumber("setNumber1").notes("notes1").name("name1").description("description1");
    }

    public static MegaSet getMegaSetSample2() {
        return new MegaSet().id(2L).setNumber("setNumber2").notes("notes2").name("name2").description("description2");
    }

    public static MegaSet getMegaSetRandomSampleGenerator() {
        return new MegaSet()
            .id(longCount.incrementAndGet())
            .setNumber(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
