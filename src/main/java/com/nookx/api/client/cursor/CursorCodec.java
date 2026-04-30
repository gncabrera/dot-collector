package com.nookx.api.client.cursor;

import com.nookx.api.repository.projection.MegaSetNewsHitProjection;
import com.nookx.api.repository.projection.MegaSetSearchHitProjection;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

public class CursorCodec {

    // -----------------------------------------------------------------
    //  format: priority|releaseDate(ISO)|id
    // -----------------------------------------------------------------

    public static String encodeCursor(MegaSetNewsHitProjection hit) {
        String rawCursor = hit.getPriority() + "|" + hit.getReleaseDate() + "|" + hit.getId();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawCursor.getBytes(StandardCharsets.UTF_8));
    }

    public static DashboardLatestReleasedCursorPosition decodeCursor(String token) {
        if (token == null || token.isBlank()) {
            return new DashboardLatestReleasedCursorPosition(null, null, null);
        }
        try {
            String rawValue = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = rawValue.split("\\|");
            if (parts.length != 3) {
                throw new BadRequestAlertException("Invalid cursor", "", "invalidcursor");
            }
            Integer priority = Integer.parseInt(parts[0]);
            LocalDate releaseDate = LocalDate.parse(parts[1]);
            Long id = Long.parseLong(parts[2]);
            return new DashboardLatestReleasedCursorPosition(priority, releaseDate, id);
        } catch (IllegalArgumentException | java.time.format.DateTimeParseException ex) {
            throw new BadRequestAlertException("Invalid cursor", "", "invalidcursor");
        }
    }

    // -----------------------------------------------------------------
    //  format: score|id
    // -----------------------------------------------------------------

    public static String encodeSearchCursor(MegaSetSearchHitProjection hit) {
        String rawCursor = hit.getScore() + "|" + hit.getId();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawCursor.getBytes(StandardCharsets.UTF_8));
    }

    public static SearchCursorPosition decodeSearchCursor(String token) {
        if (token == null || token.isBlank()) {
            return new SearchCursorPosition(null, null);
        }
        try {
            String rawValue = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] cursorParts = rawValue.split("\\|");
            if (cursorParts.length != 2) {
                throw new BadRequestAlertException("Invalid cursor", "", "invalidcursor");
            }
            Float score = Float.parseFloat(cursorParts[0]);
            Long id = Long.parseLong(cursorParts[1]);
            return new SearchCursorPosition(score, id);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestAlertException("Invalid cursor", "", "invalidcursor");
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
}
