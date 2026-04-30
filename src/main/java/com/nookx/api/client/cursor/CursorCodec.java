package com.nookx.api.client.cursor;

import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * Generic Base64-encoded keyset-pagination cursor codec.
 *
 * <p>Format: {@code part0|part1|...|partN}, URL-safe Base64, no padding. {@code null}
 * components are encoded as empty strings to keep the part count stable.</p>
 *
 * <p>To wire a new endpoint: implement {@link CursorPosition} on a record, declare
 * its {@link CursorPosition.Parser}, then call {@link #encode(CursorPosition)} /
 * {@link #decode(String, CursorPosition.Parser)} from the service.</p>
 */
public final class CursorCodec {

    private static final String SEPARATOR = "|";
    private static final String SEPARATOR_REGEX = "\\|";

    private CursorCodec() {}

    public static String encode(CursorPosition cursor) {
        String raw = Arrays.stream(cursor.parts())
            .map(part -> part == null ? "" : String.valueOf(part))
            .collect(Collectors.joining(SEPARATOR));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static <T extends CursorPosition> T decode(String token, CursorPosition.Parser<T> parser) {
        if (token == null || token.isBlank()) {
            return parser.empty();
        }
        try {
            String raw = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = raw.split(SEPARATOR_REGEX, -1);
            if (parts.length != parser.partCount()) {
                throw invalidCursor();
            }
            return parser.fromParts(parts);
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            throw invalidCursor();
        }
    }

    public static int normalizeLimit(int limit) {
        return normalizeLimit(limit, 30);
    }

    public static int normalizeLimit(int limit, int maxLimit) {
        if (limit <= 0) {
            throw new BadRequestAlertException("Search limit must be positive", "", "invalidlimit");
        }
        return Math.min(limit, maxLimit);
    }

    private static BadRequestAlertException invalidCursor() {
        return new BadRequestAlertException("Invalid cursor", "", "invalidcursor");
    }
}
