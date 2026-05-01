package com.nookx.api.repository;

import com.nookx.api.domain.BlockedProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the BlockedProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlockedProfileRepository extends JpaRepository<BlockedProfile, Long> {
    Optional<BlockedProfile> findByProfile_IdAndBlockedProfile_Id(Long profileId, Long blockedProfileId);

    boolean existsByProfile_IdAndBlockedProfile_Id(Long profileId, Long blockedProfileId);

    /**
     * True if there is any block row in either direction between the two profiles.
     */
    boolean existsByProfile_IdAndBlockedProfile_IdOrProfile_IdAndBlockedProfile_Id(
        Long profileIdA,
        Long blockedProfileIdA,
        Long profileIdB,
        Long blockedProfileIdB
    );

    @Transactional
    void deleteByProfile_IdAndBlockedProfile_Id(Long profileId, Long blockedProfileId);
}
