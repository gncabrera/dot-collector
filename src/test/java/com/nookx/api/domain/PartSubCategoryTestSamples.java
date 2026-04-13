package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PartSubCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static PartSubCategory getPartSubCategorySample1() {
        return new PartSubCategory().id(1L).name("name1").description("description1");
    }

    public static PartSubCategory getPartSubCategorySample2() {
        return new PartSubCategory().id(2L).name("name2").description("description2");
    }

    public static PartSubCategory getPartSubCategoryRandomSampleGenerator() {
        return new PartSubCategory()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
