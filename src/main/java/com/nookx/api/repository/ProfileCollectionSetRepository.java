package com.nookx.api.repository;

import com.nookx.api.domain.ProfileCollectionSet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileCollectionSet entity.
 */
@Repository
public interface ProfileCollectionSetRepository extends JpaRepository<ProfileCollectionSet, Long> {
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
}
