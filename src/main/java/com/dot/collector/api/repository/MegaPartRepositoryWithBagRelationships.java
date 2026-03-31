package com.dot.collector.api.repository;

import com.dot.collector.api.domain.MegaPart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface MegaPartRepositoryWithBagRelationships {
    Optional<MegaPart> fetchBagRelationships(Optional<MegaPart> megaPart);

    List<MegaPart> fetchBagRelationships(List<MegaPart> megaParts);

    Page<MegaPart> fetchBagRelationships(Page<MegaPart> megaParts);
}
