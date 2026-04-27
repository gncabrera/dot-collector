package com.nookx.api.repository;

import com.nookx.api.domain.MegaSet;
import com.nookx.api.repository.projection.MegaSetSearchHitProjection;
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
                GREATEST(
                    word_similarity(:searchQuery, lower(coalesce(ms.name, ''))),
                    word_similarity(:searchQuery, lower(coalesce(ms.description, ''))),
                    word_similarity(:searchQuery, lower(coalesce(ms.set_number, ''))),
                    word_similarity(:searchQuery, lower(coalesce(ms.notes, ''))),
                    word_similarity(:searchQuery, lower(coalesce(ms.attributes::text, '')))
                ) AS score
            FROM mega_set ms
            WHERE
                (ms.public_item = true OR ms.owner_id = :currentUserId)
                AND (
                    :searchQuery <% lower(coalesce(ms.name, ''))
                    OR :searchQuery <% lower(coalesce(ms.description, ''))
                    OR :searchQuery <% lower(coalesce(ms.set_number, ''))
                    OR :searchQuery <% lower(coalesce(ms.notes, ''))
                    OR :searchQuery <% lower(coalesce(ms.attributes::text, ''))
                )
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
}
