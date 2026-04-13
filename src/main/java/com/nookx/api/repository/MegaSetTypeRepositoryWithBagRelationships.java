package com.nookx.api.repository;

import com.nookx.api.domain.MegaSetType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface MegaSetTypeRepositoryWithBagRelationships {
    Optional<MegaSetType> fetchBagRelationships(Optional<MegaSetType> megaSetType);

    List<MegaSetType> fetchBagRelationships(List<MegaSetType> megaSetTypes);

    Page<MegaSetType> fetchBagRelationships(Page<MegaSetType> megaSetTypes);
}
