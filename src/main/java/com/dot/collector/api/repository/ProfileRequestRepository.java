package com.dot.collector.api.repository;

import com.dot.collector.api.domain.ProfileRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileRequestRepository extends JpaRepository<ProfileRequest, Long> {}
