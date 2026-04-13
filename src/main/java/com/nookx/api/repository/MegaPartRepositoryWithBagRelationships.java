package com.nookx.api.repository;

import com.nookx.api.domain.MegaPart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface MegaPartRepositoryWithBagRelationships {
    Optional<MegaPart> fetchBagRelationships(Optional<MegaPart> megaPart);

    List<MegaPart> fetchBagRelationships(List<MegaPart> megaParts);

    Page<MegaPart> fetchBagRelationships(Page<MegaPart> megaParts);
}
