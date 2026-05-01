package com.nookx.api.repository;

import com.nookx.api.domain.FollowingProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
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

    /**
     * Count followers of {@code profileId}, excluding rows where the follower has any block relationship
     * with {@code profileId} in either direction.
     */
    @Query(
        "SELECT COUNT(f) FROM FollowingProfile f WHERE f.followedProfile.id = :profileId AND NOT EXISTS (" +
            "SELECT 1 FROM BlockedProfile b WHERE " +
            "(b.profile.id = :profileId AND b.blockedProfile.id = f.profile.id) OR " +
            "(b.profile.id = f.profile.id AND b.blockedProfile.id = :profileId))"
    )
    long countFollowersExcludingBlocked(@Param("profileId") Long profileId);

    /**
     * Count profiles followed by {@code profileId}, excluding rows where the followed profile has any block
     * relationship with {@code profileId} in either direction.
     */
    @Query(
        "SELECT COUNT(f) FROM FollowingProfile f WHERE f.profile.id = :profileId AND NOT EXISTS (" +
            "SELECT 1 FROM BlockedProfile b WHERE " +
            "(b.profile.id = :profileId AND b.blockedProfile.id = f.followedProfile.id) OR " +
            "(b.profile.id = f.followedProfile.id AND b.blockedProfile.id = :profileId))"
    )
    long countFollowingExcludingBlocked(@Param("profileId") Long profileId);
}
