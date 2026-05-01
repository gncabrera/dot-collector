package com.nookx.api.client.cursor;

public record DashboardPopularCollectionsCursorPosition(Long score, Long id) implements CursorPosition {
    public static final Parser<DashboardPopularCollectionsCursorPosition> PARSER = new Parser<>() {
        @Override
        public int partCount() {
            return 2;
        }

        @Override
        public DashboardPopularCollectionsCursorPosition empty() {
            return new DashboardPopularCollectionsCursorPosition(null, null);
        }

        @Override
        public DashboardPopularCollectionsCursorPosition fromParts(String[] parts) {
            return new DashboardPopularCollectionsCursorPosition(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        }
    };

    @Override
    public Object[] parts() {
        return new Object[] { score, id };
    }
}
