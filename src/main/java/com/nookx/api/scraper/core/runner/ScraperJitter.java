package com.nookx.api.scraper.core.runner;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Shared helper that adds uniform jitter to scheduler delays so the scraper does not hit the
 * source with a perfectly periodic cadence.
 */
final class ScraperJitter {

    private ScraperJitter() {}

    /**
     * Returns a random non-negative sleep in ms bounded by {@code jitterMs}. Used by runners to
     * spread their tick across the configured window.
     */
    static long pickJitter(long jitterMs) {
        if (jitterMs <= 0) {
            return 0;
        }
        return ThreadLocalRandom.current().nextLong(0, jitterMs + 1);
    }
}
