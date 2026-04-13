package com.nookx.api.repository;

import com.nookx.api.domain.ProfileRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileRequestRepository extends JpaRepository<ProfileRequest, Long> {}
