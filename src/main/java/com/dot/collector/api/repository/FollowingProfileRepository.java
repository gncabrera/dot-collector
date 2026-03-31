package com.dot.collector.api.repository;

import com.dot.collector.api.domain.FollowingProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FollowingProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FollowingProfileRepository extends JpaRepository<FollowingProfile, Long> {}
