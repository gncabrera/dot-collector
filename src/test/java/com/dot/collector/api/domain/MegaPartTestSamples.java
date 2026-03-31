package com.dot.collector.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MegaPartTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MegaPart getMegaPartSample1() {
        return new MegaPart()
            .id(1L)
            .partNumber("partNumber1")
            .nameEN("nameEN1")
            .nameES("nameES1")
            .nameDE("nameDE1")
            .nameFR("nameFR1")
            .description("description1")
            .notes("notes1");
    }

    public static MegaPart getMegaPartSample2() {
        return new MegaPart()
            .id(2L)
            .partNumber("partNumber2")
            .nameEN("nameEN2")
            .nameES("nameES2")
            .nameDE("nameDE2")
            .nameFR("nameFR2")
            .description("description2")
            .notes("notes2");
    }

    public static MegaPart getMegaPartRandomSampleGenerator() {
        return new MegaPart()
            .id(longCount.incrementAndGet())
            .partNumber(UUID.randomUUID().toString())
            .nameEN(UUID.randomUUID().toString())
            .nameES(UUID.randomUUID().toString())
            .nameDE(UUID.randomUUID().toString())
            .nameFR(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
