package com.dot.collector.api.repository;

import com.dot.collector.api.domain.MegaAsset;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaAsset entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MegaAssetRepository extends JpaRepository<MegaAsset, Long> {}
