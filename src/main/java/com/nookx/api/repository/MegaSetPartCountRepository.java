package com.nookx.api.repository;

import com.nookx.api.domain.MegaSetPartCount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaSetPartCount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MegaSetPartCountRepository extends JpaRepository<MegaSetPartCount, Long> {}
