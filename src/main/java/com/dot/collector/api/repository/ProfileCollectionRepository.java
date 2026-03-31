package com.dot.collector.api.repository;

import com.dot.collector.api.domain.ProfileCollection;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileCollection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileCollectionRepository extends JpaRepository<ProfileCollection, Long> {}
