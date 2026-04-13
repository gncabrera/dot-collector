package com.nookx.api.repository;

import com.nookx.api.domain.FollowingProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FollowingProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FollowingProfileRepository extends JpaRepository<FollowingProfile, Long> {}
