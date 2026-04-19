package com.nookx.api.repository;

import com.nookx.api.domain.ProfileInterest;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileInterest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileInterestRepository extends JpaRepository<ProfileInterest, Long> {
    boolean existsByProfile_IdAndInterest_Id(Long profileId, Long interestId);

    long deleteByProfile_IdAndInterest_IdIn(Long profileId, Collection<Long> interestIds);

    @Query("SELECT pi.interest.id FROM ProfileInterest pi WHERE pi.profile.id = :profileId")
    List<Long> findInterestIdsByProfileId(@Param("profileId") Long profileId);
}
