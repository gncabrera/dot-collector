package com.nookx.api.repository;

import com.nookx.api.domain.FollowingProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the FollowingProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FollowingProfileRepository extends JpaRepository<FollowingProfile, Long> {
    Optional<FollowingProfile> findByProfile_IdAndFollowedProfile_Id(Long profileId, Long followedProfileId);

    boolean existsByProfile_IdAndFollowedProfile_Id(Long profileId, Long followedProfileId);

    @Transactional
    void deleteByProfile_IdAndFollowedProfile_Id(Long profileId, Long followedProfileId);

    /**
     * Delete the follow rows in either direction between two profiles.
     */
    @Transactional
    void deleteByProfile_IdAndFollowedProfile_IdOrProfile_IdAndFollowedProfile_Id(
        Long profileIdA,
        Long followedProfileIdA,
        Long profileIdB,
        Long followedProfileIdB
    );
}
