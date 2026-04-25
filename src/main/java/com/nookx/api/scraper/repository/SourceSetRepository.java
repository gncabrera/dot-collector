package com.nookx.api.scraper.repository;

import com.nookx.api.scraper.domain.SourceSet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceSetRepository extends JpaRepository<SourceSet, Long> {
    Optional<SourceSet> findBySourceCodeAndSourceExternalId(String sourceCode, String sourceExternalId);
}
