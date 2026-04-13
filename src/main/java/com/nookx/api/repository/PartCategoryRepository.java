package com.nookx.api.repository;

import com.nookx.api.domain.PartCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartCategoryRepository extends JpaRepository<PartCategory, Long> {}
