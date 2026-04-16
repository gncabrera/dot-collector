package com.nookx.api.repository;

import com.nookx.api.domain.MegaAsset;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaAsset entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MegaAssetRepository extends JpaRepository<MegaAsset, Long> {
    Optional<MegaAsset> findByUuid(String uuid);
}
