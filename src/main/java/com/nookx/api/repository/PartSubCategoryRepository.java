package com.nookx.api.repository;

import com.nookx.api.domain.PartSubCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartSubCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartSubCategoryRepository extends JpaRepository<PartSubCategory, Long> {}
