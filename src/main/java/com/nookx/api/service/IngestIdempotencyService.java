package com.nookx.api.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

/**
 * In-memory idempotency for ingest endpoints keyed by (scope + idempotency key).
 */
@Service
public class IngestIdempotencyService {

    private final ConcurrentMap<String, CachedResponse<?>> responsesByKey = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Object> locksByKey = new ConcurrentHashMap<>();

    public <T> CachedResponse<T> getOrCompute(String scope, String idempotencyKey, Supplier<CachedResponse<T>> supplier) {
        String scopedKey = scope + ":" + idempotencyKey;
        CachedResponse<?> existing = responsesByKey.get(scopedKey);
        if (existing != null) {
            return cast(existing);
        }

        Object lock = locksByKey.computeIfAbsent(scopedKey, ignored -> new Object());
        synchronized (lock) {
            try {
                CachedResponse<?> secondCheck = responsesByKey.get(scopedKey);
                if (secondCheck != null) {
                    return cast(secondCheck);
                }
                CachedResponse<T> computed = supplier.get();
                if (isCacheable(computed)) {
                    responsesByKey.put(scopedKey, computed);
                }
                return computed;
            } finally {
                locksByKey.remove(scopedKey, lock);
            }
        }
    }

    private static boolean isCacheable(CachedResponse<?> response) {
        return response != null && response.status() != null && response.status().is2xxSuccessful() && response.status().value() != 207;
    }

    @SuppressWarnings("unchecked")
    private static <T> CachedResponse<T> cast(CachedResponse<?> cachedResponse) {
        return (CachedResponse<T>) cachedResponse;
    }

    public record CachedResponse<T>(HttpStatusCode status, T body) {}
}
