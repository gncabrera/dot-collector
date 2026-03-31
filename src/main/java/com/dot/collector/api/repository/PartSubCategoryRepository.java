package com.dot.collector.api.repository;

import com.dot.collector.api.domain.PartSubCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartSubCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartSubCategoryRepository extends JpaRepository<PartSubCategory, Long> {}
