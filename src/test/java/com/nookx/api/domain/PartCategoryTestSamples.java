package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PartCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static PartCategory getPartCategorySample1() {
        return new PartCategory().id(1L).name("name1").description("description1");
    }

    public static PartCategory getPartCategorySample2() {
        return new PartCategory().id(2L).name("name2").description("description2");
    }

    public static PartCategory getPartCategoryRandomSampleGenerator() {
        return new PartCategory()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
