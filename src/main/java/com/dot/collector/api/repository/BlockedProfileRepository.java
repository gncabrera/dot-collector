package com.dot.collector.api.repository;

import com.dot.collector.api.domain.BlockedProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BlockedProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlockedProfileRepository extends JpaRepository<BlockedProfile, Long> {}
