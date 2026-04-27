package com.nookx.api.repository;

import com.nookx.api.domain.MegaSet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaSet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MegaSetRepository extends JpaRepository<MegaSet, Long> {}
