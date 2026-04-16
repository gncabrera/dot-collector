package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MegaAssetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MegaAsset getMegaAssetSample1() {
        MegaAsset m = new MegaAsset()
            .id(1L)
            .name("name1")
            .description("description1")
            .uuid(UUID.fromString("00000000-0000-0000-0000-000000000001"))
            .extension(".txt")
            .contentType("text/plain")
            .sizeBytes(1L);
        m.setPublic(false);
        return m;
    }

    public static MegaAsset getMegaAssetSample2() {
        MegaAsset m = new MegaAsset()
            .id(2L)
            .name("name2")
            .description("description2")
            .uuid(UUID.fromString("00000000-0000-0000-0000-000000000002"))
            .extension(".png")
            .contentType("image/png")
            .sizeBytes(2L);
        m.setPublic(true);
        return m;
    }

    public static MegaAsset getMegaAssetRandomSampleGenerator() {
        MegaAsset m = new MegaAsset()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .uuid(UUID.randomUUID())
            .extension(".bin")
            .contentType("application/octet-stream")
            .sizeBytes(random.nextLong());
        m.setPublic(random.nextBoolean());
        return m;
    }
}
