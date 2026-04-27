package com.nookx.api.repository;

import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.ProfileCollectionSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileCollectionSet entity.
 */
@Repository
public interface ProfileCollectionSetRepository extends JpaRepository<ProfileCollectionSet, Long> {
    List<ProfileCollectionSet> findByCollection_Id(Long collectionId);

    /** Unique profiles that have flagged this set as owned. */
    @Query(
        "select count(distinct pcs.collection.profile.id) " +
            "from ProfileCollectionSet pcs " +
            "where pcs.set.id = :setId and pcs.owned = true"
    )
    long countDistinctOwnersBySetId(Long setId);

    /** Unique profiles that have flagged this set as wanted. */
    @Query(
        "select count(distinct pcs.collection.profile.id) " +
            "from ProfileCollectionSet pcs " +
            "where pcs.set.id = :setId and pcs.wanted = true"
    )
    long countDistinctWantersBySetId(Long setId);

    /** Unique collections that include this set (regardless of owned/wanted). */
    @Query("select count(distinct pcs.collection.id) from ProfileCollectionSet pcs where pcs.set.id = :setId")
    long countDistinctCollectionsBySetId(Long setId);

    boolean existsByCollection_IdAndSet_Id(Long collectionId, Long setId);

    Optional<ProfileCollectionSet> findByCollection_IdAndSet_Id(Long collectionId, Long setId);

    @Query("select pcs.set from ProfileCollectionSet pcs where pcs.collection.id = :collectionId")
    List<MegaSet> findSetsByCollectionId(@Param("collectionId") Long collectionId);
}
