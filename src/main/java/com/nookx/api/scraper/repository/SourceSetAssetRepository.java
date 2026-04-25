package com.nookx.api.scraper.repository;

import com.nookx.api.scraper.domain.SourceSet;
import com.nookx.api.scraper.domain.SourceSetAsset;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceSetAssetRepository extends JpaRepository<SourceSetAsset, Long> {
    Optional<SourceSetAsset> findBySourceSetAndExternalUrl(SourceSet sourceSet, String externalUrl);

    Optional<SourceSetAsset> findFirstByContentHash(String contentHash);

    List<SourceSetAsset> findByDownloadedFalseAndDownloadFailedFalse(Pageable pageable);

    List<SourceSetAsset> findBySourceSet(SourceSet sourceSet);
}
