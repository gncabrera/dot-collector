package com.nookx.api.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Profile getProfileSample1() {
        return new Profile()
            .id(1L)
            .username("username1")
            .fullName("fullName1")
            .location("location1")
            .email("email1")
            .instagram("instagram1")
            .facebook("facebook1")
            .whatsapp("whatsapp1");
    }

    public static Profile getProfileSample2() {
        return new Profile()
            .id(2L)
            .username("username2")
            .fullName("fullName2")
            .location("location2")
            .email("email2")
            .instagram("instagram2")
            .facebook("facebook2")
            .whatsapp("whatsapp2");
    }

    public static Profile getProfileRandomSampleGenerator() {
        return new Profile()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .location(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .instagram(UUID.randomUUID().toString())
            .facebook(UUID.randomUUID().toString())
            .whatsapp(UUID.randomUUID().toString());
    }
}
