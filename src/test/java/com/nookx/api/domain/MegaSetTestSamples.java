package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MegaSetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MegaSet getMegaSetSample1() {
        return new MegaSet()
            .id(1L)
            .setNumber("setNumber1")
            .notes("notes1")
            .nameEN("nameEN1")
            .nameES("nameES1")
            .nameDE("nameDE1")
            .nameFR("nameFR1")
            .descriptionEN("descriptionEN1")
            .descriptionES("descriptionES1")
            .descriptionDE("descriptionDE1")
            .descriptionFR("descriptionFR1");
    }

    public static MegaSet getMegaSetSample2() {
        return new MegaSet()
            .id(2L)
            .setNumber("setNumber2")
            .notes("notes2")
            .nameEN("nameEN2")
            .nameES("nameES2")
            .nameDE("nameDE2")
            .nameFR("nameFR2")
            .descriptionEN("descriptionEN2")
            .descriptionES("descriptionES2")
            .descriptionDE("descriptionDE2")
            .descriptionFR("descriptionFR2");
    }

    public static MegaSet getMegaSetRandomSampleGenerator() {
        return new MegaSet()
            .id(longCount.incrementAndGet())
            .setNumber(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .nameEN(UUID.randomUUID().toString())
            .nameES(UUID.randomUUID().toString())
            .nameDE(UUID.randomUUID().toString())
            .nameFR(UUID.randomUUID().toString())
            .descriptionEN(UUID.randomUUID().toString())
            .descriptionES(UUID.randomUUID().toString())
            .descriptionDE(UUID.randomUUID().toString())
            .descriptionFR(UUID.randomUUID().toString());
    }
}
