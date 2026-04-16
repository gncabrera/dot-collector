package com.nookx.api.repository;

import com.nookx.api.domain.ProfileInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileInterest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileInterestRepository extends JpaRepository<ProfileInterest, Long> {}
