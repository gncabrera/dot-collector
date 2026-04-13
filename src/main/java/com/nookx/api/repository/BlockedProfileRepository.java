package com.nookx.api.repository;

import com.nookx.api.domain.BlockedProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BlockedProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlockedProfileRepository extends JpaRepository<BlockedProfile, Long> {}
