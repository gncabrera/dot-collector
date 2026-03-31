package com.dot.collector.api.repository;

import com.dot.collector.api.domain.MegaAttribute;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaAttribute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MegaAttributeRepository extends JpaRepository<MegaAttribute, Long> {}
