package com.nookx.api.repository;

import com.nookx.api.domain.MegaPartSubPartCount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaPartSubPartCount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MegaPartSubPartCountRepository extends JpaRepository<MegaPartSubPartCount, Long> {}
