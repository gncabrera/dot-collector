package com.nookx.api.repository;

import com.nookx.api.domain.MegaSet;
import com.nookx.api.repository.projection.MegaSetNewsHitProjection;
import com.nookx.api.repository.projection.MegaSetSearchHitProjection;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaSet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MegaSetRepository extends JpaRepository<MegaSet, Long> {
    @Query(
        value = """
        WITH ranked_sets AS (
            SELECT
                ms.id AS id,
                word_similarity(lower(f_unaccent(:searchQuery)), ms.search_text) AS score
            FROM mega_set ms
            WHERE
                (ms.public_item = true OR ms.owner_id = :currentUserId)
                AND lower(f_unaccent(:searchQuery)) <% ms.search_text
        )
        SELECT rs.id AS id, rs.score AS score
        FROM ranked_sets rs
        WHERE (
            :cursorScore IS NULL
            OR rs.score < :cursorScore
            OR (rs.score = :cursorScore AND rs.id < :cursorId)
        )
        ORDER BY rs.score DESC, rs.id DESC
        LIMIT :limitPlusOne
        """,
        nativeQuery = true
    )
    List<MegaSetSearchHitProjection> searchSetHits(
        @Param("searchQuery") String searchQuery,
        @Param("currentUserId") Long currentUserId,
        @Param("cursorScore") Float cursorScore,
        @Param("cursorId") Long cursorId,
        @Param("limitPlusOne") int limitPlusOne
    );

    /**
     * Latest released public sets ordered by release date desc. Sets whose interest matches
     * one of {@code interestIds} are boosted (priority=1) above the rest (priority=0),
     * so a user's subscribed interests surface first within the same release window.
     *
     * <p>Cursor pagination is stable on the tuple (priority desc, release_date desc, id desc).
     * Pass {@code null} for every cursor parameter to fetch the first page.</p>
     *
     * <p>{@code interestIds} must contain at least one value – callers pass a sentinel
     * (e.g. {@code -1}) when the user has no subscribed interests so the {@code IN} clause
     * stays valid and priority resolves to 0 for every row.</p>
     */
    @Query(
        value = """
        WITH scored AS (
            SELECT
                ms.id AS id,
                ms.release_date AS release_date,
                CASE WHEN ms.interest_id IN (:interestIds) THEN 1 ELSE 0 END AS priority
            FROM mega_set ms
            WHERE ms.public_item = true
              AND ms.release_date IS NOT NULL
        )
        SELECT s.id AS id, s.release_date AS releaseDate, s.priority AS priority
        FROM scored s
        WHERE (
            :cursorPriority IS NULL
            OR s.priority < :cursorPriority
            OR (s.priority = :cursorPriority AND s.release_date < :cursorDate)
            OR (s.priority = :cursorPriority AND s.release_date = :cursorDate AND s.id < :cursorId)
        )
        ORDER BY s.priority DESC, s.release_date DESC, s.id DESC
        LIMIT :limitPlusOne
        """,
        nativeQuery = true
    )
    List<MegaSetNewsHitProjection> findDashboardNewsSets(
        @Param("interestIds") Collection<Long> interestIds,
        @Param("cursorPriority") Integer cursorPriority,
        @Param("cursorDate") LocalDate cursorDate,
        @Param("cursorId") Long cursorId,
        @Param("limitPlusOne") int limitPlusOne
    );
}
