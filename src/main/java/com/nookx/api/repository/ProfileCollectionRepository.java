package com.nookx.api.repository;

import com.nookx.api.domain.ProfileCollection;
import com.nookx.api.domain.enumeration.ProfileCollectionType;
import com.nookx.api.repository.projection.PopularCollectionHitProjection;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileCollection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileCollectionRepository extends JpaRepository<ProfileCollection, Long> {
    long countByProfile_IdAndType(Long profileId, ProfileCollectionType type);

    List<ProfileCollection> findByProfile_Id(Long profileId);

    /**
     * Popular collections ranked by a weighted sum of recent vs older interactions.
     *
     * <p>Each interaction (currently a star or a clone) contributes {@code 3} points when it
     * happened in the last two days and {@code 1} point otherwise. New interaction sources can
     * be added to the {@code interactions} CTE as a new {@code UNION ALL} branch following the
     * same {@code (collection_id, weight)} shape.</p>
     *
     * <p>Cursor pagination is stable on the tuple {@code (score desc, id desc)}. Pass
     * {@code null} for both cursor parameters to fetch the first page. Collections without any
     * interaction are excluded from the result.</p>
     */
    @Query(
        value = """
        WITH interactions AS (
            SELECT pcs.profile_collection_id AS collection_id,
                   CASE WHEN pcs.date_starred >= NOW() - INTERVAL '2 days' THEN 3 ELSE 1 END AS weight
            FROM profile_collection_star pcs
            UNION ALL
            SELECT ci.source_collection_id AS collection_id,
                   CASE WHEN ci.cloned_at >= NOW() - INTERVAL '2 days' THEN 3 ELSE 1 END AS weight
            FROM clone_information ci
            WHERE ci.cloned = true
              AND ci.source_collection_id IS NOT NULL
        ),
        scored AS (
            SELECT i.collection_id AS id, SUM(i.weight) AS score
            FROM interactions i
            GROUP BY i.collection_id
        )
        SELECT s.id AS id, s.score AS score
        FROM scored s
        WHERE (
            :cursorScore IS NULL
            OR s.score < :cursorScore
            OR (s.score = :cursorScore AND s.id < :cursorId)
        )
        ORDER BY s.score DESC, s.id DESC
        LIMIT :limitPlusOne
        """,
        nativeQuery = true
    )
    List<PopularCollectionHitProjection> findPopularCollections(
        @Param("cursorScore") Long cursorScore,
        @Param("cursorId") Long cursorId,
        @Param("limitPlusOne") int limitPlusOne
    );
}
