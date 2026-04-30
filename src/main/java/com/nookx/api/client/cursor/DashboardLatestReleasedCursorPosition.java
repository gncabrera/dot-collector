package com.nookx.api.client.cursor;

import java.time.LocalDate;

public record DashboardLatestReleasedCursorPosition(Integer priority, LocalDate releaseDate, Long id) implements CursorPosition {
    public static final Parser<DashboardLatestReleasedCursorPosition> PARSER = new Parser<>() {
        @Override
        public int partCount() {
            return 3;
        }

        @Override
        public DashboardLatestReleasedCursorPosition empty() {
            return new DashboardLatestReleasedCursorPosition(null, null, null);
        }

        @Override
        public DashboardLatestReleasedCursorPosition fromParts(String[] parts) {
            return new DashboardLatestReleasedCursorPosition(
                Integer.parseInt(parts[0]),
                LocalDate.parse(parts[1]),
                Long.parseLong(parts[2])
            );
        }
    };

    @Override
    public Object[] parts() {
        return new Object[] { priority, releaseDate, id };
    }
}
