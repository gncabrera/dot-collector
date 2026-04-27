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
                    similarity(lower(coalesce(ms.name, '')), :searchQuery),
                    similarity(lower(coalesce(ms.description, '')), :searchQuery),
                    similarity(lower(coalesce(ms.set_number, '')), :searchQuery),
                    similarity(lower(coalesce(ms.notes, '')), :searchQuery),
                    similarity(lower(coalesce(ms.attributes::text, '')), :searchQuery)
                ) AS score
            FROM mega_set ms
            WHERE
                lower(coalesce(ms.name, '')) % :searchQuery
                OR lower(coalesce(ms.description, '')) % :searchQuery
                OR lower(coalesce(ms.set_number, '')) % :searchQuery
                OR lower(coalesce(ms.notes, '')) % :searchQuery
                OR lower(coalesce(ms.attributes::text, '')) % :searchQuery
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
        @Param("cursorScore") Float cursorScore,
        @Param("cursorId") Long cursorId,
        @Param("limitPlusOne") int limitPlusOne
    );
}
