package com.nookx.api.repository;

import com.nookx.api.domain.MegaSetFile;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaSetFile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MegaSetFileRepository extends JpaRepository<MegaSetFile, Long> {
    List<MegaSetFile> findByMegaSet_IdOrderBySortOrderAsc(Long setId);
}
