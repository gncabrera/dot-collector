package com.nookx.api.repository;

import com.nookx.api.domain.ProfileCollectionSet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileCollectionSet entity.
 */
@Repository
public interface ProfileCollectionSetRepository extends JpaRepository<ProfileCollectionSet, Long> {}
