package com.nookx.api;

import com.nookx.api.config.AsyncSyncConfiguration;
import com.nookx.api.config.DatabaseTestcontainer;
import com.nookx.api.config.JacksonConfiguration;
import com.nookx.api.config.JacksonHibernateConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { NookxApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, JacksonHibernateConfiguration.class })
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
