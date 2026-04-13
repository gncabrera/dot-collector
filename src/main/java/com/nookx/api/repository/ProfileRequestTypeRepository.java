package com.nookx.api.repository;

import com.nookx.api.domain.ProfileRequestType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileRequestType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileRequestTypeRepository extends JpaRepository<ProfileRequestType, Long> {}
