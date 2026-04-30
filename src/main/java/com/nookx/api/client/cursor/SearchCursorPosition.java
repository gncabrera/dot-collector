package com.nookx.api.client.cursor;

public record SearchCursorPosition(Float score, Long id) implements CursorPosition {
    public static final Parser<SearchCursorPosition> PARSER = new Parser<>() {
        @Override
        public int partCount() {
            return 2;
        }

        @Override
        public SearchCursorPosition empty() {
            return new SearchCursorPosition(null, null);
        }

        @Override
        public SearchCursorPosition fromParts(String[] parts) {
            return new SearchCursorPosition(Float.parseFloat(parts[0]), Long.parseLong(parts[1]));
        }
    };

    @Override
    public Object[] parts() {
        return new Object[] { score, id };
    }
}
