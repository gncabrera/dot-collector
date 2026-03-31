package com.dot.collector.api.repository;

import com.dot.collector.api.domain.MegaPartType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface MegaPartTypeRepositoryWithBagRelationships {
    Optional<MegaPartType> fetchBagRelationships(Optional<MegaPartType> megaPartType);

    List<MegaPartType> fetchBagRelationships(List<MegaPartType> megaPartTypes);

    Page<MegaPartType> fetchBagRelationships(Page<MegaPartType> megaPartTypes);
}
