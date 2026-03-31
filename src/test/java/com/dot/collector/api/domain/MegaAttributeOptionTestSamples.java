package com.dot.collector.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MegaAttributeOptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MegaAttributeOption getMegaAttributeOptionSample1() {
        return new MegaAttributeOption().id(1L).label("label1").value("value1").description("description1");
    }

    public static MegaAttributeOption getMegaAttributeOptionSample2() {
        return new MegaAttributeOption().id(2L).label("label2").value("value2").description("description2");
    }

    public static MegaAttributeOption getMegaAttributeOptionRandomSampleGenerator() {
        return new MegaAttributeOption()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .value(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
