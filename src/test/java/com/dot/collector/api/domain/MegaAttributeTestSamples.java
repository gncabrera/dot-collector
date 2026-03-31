package com.dot.collector.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MegaAttributeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MegaAttribute getMegaAttributeSample1() {
        return new MegaAttribute()
            .id(1L)
            .name("name1")
            .label("label1")
            .description("description1")
            .defaultValue("defaultValue1")
            .minLength(1)
            .maxLength(1)
            .regex("regex1")
            .order(1)
            .attributeGroup("attributeGroup1");
    }

    public static MegaAttribute getMegaAttributeSample2() {
        return new MegaAttribute()
            .id(2L)
            .name("name2")
            .label("label2")
            .description("description2")
            .defaultValue("defaultValue2")
            .minLength(2)
            .maxLength(2)
            .regex("regex2")
            .order(2)
            .attributeGroup("attributeGroup2");
    }

    public static MegaAttribute getMegaAttributeRandomSampleGenerator() {
        return new MegaAttribute()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .label(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .defaultValue(UUID.randomUUID().toString())
            .minLength(intCount.incrementAndGet())
            .maxLength(intCount.incrementAndGet())
            .regex(UUID.randomUUID().toString())
            .order(intCount.incrementAndGet())
            .attributeGroup(UUID.randomUUID().toString());
    }
}
