package com.nookx.api.client.cursor;

/**
 * Marker contract for opaque keyset-pagination cursors.
 *
 * <p>Implementations are simple records that expose their tuple as {@link #parts()},
 * in the exact order used by the SQL ordering. To register a new cursor type:
 *
 * <ol>
 *   <li>Create a {@code record} implementing this interface.</li>
 *   <li>Implement {@link #parts()} returning the values in ordering order.</li>
 *   <li>Expose a {@code public static final Parser<T> PARSER} that knows how to
 *       parse the parts back and provide the empty (first-page) instance.</li>
 * </ol>
 *
 * <p>Encoding/decoding is then handled by {@link CursorCodec#encode(CursorPosition)}
 * and {@link CursorCodec#decode(String, Parser)}.
 */
public interface CursorPosition {
    /**
     * Tuple of values that make up the cursor, in ordering order.
     * {@code null} entries are encoded as empty strings.
     */
    Object[] parts();

    /**
     * Knows how to materialize a {@link CursorPosition} subtype from its raw parts
     * and how to build the empty (first-page) instance.
     */
    interface Parser<T extends CursorPosition> {
        /** Number of parts this cursor type produces. */
        int partCount();

        /** Empty cursor used when no token is supplied (first page). */
        T empty();

        /** Build a cursor from its raw string parts (length == {@link #partCount()}). */
        T fromParts(String[] parts);
    }
}
