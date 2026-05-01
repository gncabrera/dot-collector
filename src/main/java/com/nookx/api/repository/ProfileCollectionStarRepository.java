package com.nookx.api.repository;

import com.nookx.api.domain.ProfileCollectionStar;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the {@link ProfileCollectionStar} entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileCollectionStarRepository extends JpaRepository<ProfileCollectionStar, Long> {
    Optional<ProfileCollectionStar> findByProfile_IdAndProfileCollection_Id(Long profileId, Long profileCollectionId);

    long countByProfileCollection_Id(Long profileCollectionId);

    @Transactional
    void deleteByProfile_IdAndProfileCollection_Id(Long profileId, Long profileCollectionId);

    @Transactional
    void deleteByProfileCollection_Id(Long profileCollectionId);
}
