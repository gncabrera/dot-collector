package com.nookx.api.scraper.repository;

import com.nookx.api.scraper.domain.MegaSetSourceLink;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MegaSetSourceLinkRepository extends JpaRepository<MegaSetSourceLink, Long> {
    Optional<MegaSetSourceLink> findBySourceCodeAndSourceExternalId(String sourceCode, String sourceExternalId);
}
