package com.nookx.api.repository;

import com.nookx.api.domain.CloneInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaAttribute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CloneInformationRepository extends JpaRepository<CloneInformation, Long> {}
