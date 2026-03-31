package com.dot.collector.api.repository;

import com.dot.collector.api.domain.PartCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartCategoryRepository extends JpaRepository<PartCategory, Long> {}
