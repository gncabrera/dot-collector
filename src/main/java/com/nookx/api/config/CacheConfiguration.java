package com.nookx.api.config;

import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Lightweight in-memory cache configuration.
 *
 * <p>Used primarily to cache versioned dynamic schemas (see
 * {@link com.nookx.api.service.MegaSetTypeService}) since those are read-heavy
 * and only invalidated when a new version is published.</p>
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String MEGA_SET_TYPES = "megaSetTypes";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(new ConcurrentMapCache(MEGA_SET_TYPES)));
        return manager;
    }
}
