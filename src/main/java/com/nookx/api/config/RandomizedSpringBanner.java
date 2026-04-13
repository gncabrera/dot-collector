package com.nookx.api.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.ResourceBanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

/**
 * Picks a random ASCII banner from {@code classpath:banner-variants/banner-*.txt} and injects a random
 * tagline from {@code classpath:banner-variants/taglines.json} into the {@code {{TAGLINE}}} placeholder.
 * Spring placeholders (e.g. {@code ${spring-boot.version}}) are resolved by {@link ResourceBanner}.
 */
public class RandomizedSpringBanner implements Banner {

    private static final Logger LOG = LoggerFactory.getLogger(RandomizedSpringBanner.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String VARIANT_GLOB = "classpath:banner-variants/banner-*.txt";
    private static final String TAGLINES_RESOURCE = "banner-variants/taglines.json";
    private static final String PLACEHOLDER = "{{TAGLINE}}";

    private static final String FALLBACK_BANNER = """
        ${AnsiColor.BRIGHT_GREEN}
          NOOKX
        ${AnsiColor.BRIGHT_YELLOW}  "We stack dots so your shelves don't have to."${AnsiColor.DEFAULT}

        ${AnsiColor.BRIGHT_BLUE}:: Running Spring Boot ${spring-boot.version} :: Profile(s) ${spring.profiles.active} ::${AnsiColor.DEFAULT}
        """;

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        String body;
        try {
            body = buildBannerBody();
        } catch (IOException e) {
            LOG.warn("Could not build randomized banner, using fallback: {}", e.getMessage());
            body = FALLBACK_BANNER;
        }
        ResourceBanner delegate = new ResourceBanner(new ByteArrayResource(body.getBytes(StandardCharsets.UTF_8)));
        delegate.printBanner(environment, sourceClass, out);
    }

    private String buildBannerBody() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] variants = resolver.getResources(VARIANT_GLOB);
        if (variants.length == 0) {
            throw new IOException("No resources matched " + VARIANT_GLOB);
        }
        Resource picked = variants[ThreadLocalRandom.current().nextInt(variants.length)];
        String template = StreamUtils.copyToString(picked.getInputStream(), StandardCharsets.UTF_8);
        String tagline = pickRandomTagline();
        String coloredTagline = "${AnsiColor.BRIGHT_YELLOW}  \"" + escapeForBanner(tagline) + "\"${AnsiColor.DEFAULT}";
        if (LOG.isDebugEnabled()) {
            LOG.debug("Banner variant: {}, tagline: {}", picked.getFilename(), tagline);
        }
        return template.replace(PLACEHOLDER, coloredTagline);
    }

    private String pickRandomTagline() throws IOException {
        ClassPathResource resource = new ClassPathResource(TAGLINES_RESOURCE);
        if (!resource.exists()) {
            return "We stack dots so your shelves don't have to.";
        }
        List<String> lines = OBJECT_MAPPER.readValue(resource.getInputStream(), new TypeReference<>() {});
        if (lines == null || lines.isEmpty()) {
            return "We stack dots so your shelves don't have to.";
        }
        return lines.get(ThreadLocalRandom.current().nextInt(lines.size()));
    }

    /** Escape backslashes and quotes so tagline is safe inside {@code "..."} in the banner file. */
    private static String escapeForBanner(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
