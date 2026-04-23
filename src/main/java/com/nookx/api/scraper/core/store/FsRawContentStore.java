package com.nookx.api.scraper.core.store;

import com.nookx.api.config.ApplicationProperties;
import com.nookx.api.scraper.domain.enumeration.PageType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Filesystem backed {@link RawContentStore} that gzips every payload.
 * <p>
 * Layout under the configured base directory:
 * <pre>
 *   {base}/{sourceCode}/{pageType}/{keyPrefix}/{key}.html.gz
 * </pre>
 * {@code keyPrefix} is the first two characters of the sanitized key, which keeps directory fan-out
 * manageable once we have tens of thousands of files (cf. git pack layout).
 */
@Component
@Profile("scraper")
public class FsRawContentStore implements RawContentStore {

    private static final Logger LOG = LoggerFactory.getLogger(FsRawContentStore.class);

    private final Path baseDir;

    public FsRawContentStore(ApplicationProperties applicationProperties) {
        this.baseDir = Path.of(applicationProperties.getScraper().getRawStorageDirectory()).toAbsolutePath().normalize();
    }

    @Override
    public String store(String sourceCode, PageType pageType, String naturalKey, String fallbackKey, byte[] bytes) {
        String safeKey = sanitize(naturalKey != null && !naturalKey.isBlank() ? naturalKey : fallbackKey);
        String prefix = safeKey.length() >= 2 ? safeKey.substring(0, 2) : "__";
        Path dir = baseDir.resolve(sanitize(sourceCode)).resolve(pageType.name().toLowerCase(Locale.ROOT)).resolve(prefix);
        String filename = safeKey + ".html.gz";
        Path target = dir.resolve(filename).normalize();
        if (!target.startsWith(baseDir)) {
            throw new IllegalStateException("Computed path escapes base dir: " + target);
        }
        try {
            Files.createDirectories(dir);
            try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
                ByteArrayOutputStream gzBuffer = new ByteArrayOutputStream(bytes.length / 4 + 32);
                try (GZIPOutputStream gz = new GZIPOutputStream(gzBuffer)) {
                    in.transferTo(gz);
                }
                Files.write(target, gzBuffer.toByteArray());
            }
            Path relative = baseDir.relativize(target);
            LOG.debug("Stored {} bytes at {}", bytes.length, relative);
            return relative.toString().replace('\\', '/');
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store raw content at " + target, e);
        }
    }

    @Override
    public byte[] read(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return null;
        }
        Path file = baseDir.resolve(storagePath).normalize();
        if (!file.startsWith(baseDir) || !Files.isRegularFile(file)) {
            return null;
        }
        try (GZIPInputStream in = new GZIPInputStream(Files.newInputStream(file))) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read raw content at " + file, e);
        }
    }

    private static String sanitize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "__";
        }
        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length() && sb.length() < 128; i++) {
            char c = raw.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.') {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        return sb.isEmpty() ? "__" : sb.toString();
    }
}
