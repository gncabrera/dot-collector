package com.nookx.api.repository;

import com.nookx.api.domain.ProfileImage;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileImage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByProfile_Id(Long profileId);
}
