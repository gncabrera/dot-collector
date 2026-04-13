package com.nookx.api.repository;

import com.nookx.api.domain.ProfileCollectionSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ProfileCollectionSetRepositoryWithBagRelationships {
    Optional<ProfileCollectionSet> fetchBagRelationships(Optional<ProfileCollectionSet> profileCollectionSet);

    List<ProfileCollectionSet> fetchBagRelationships(List<ProfileCollectionSet> profileCollectionSets);

    Page<ProfileCollectionSet> fetchBagRelationships(Page<ProfileCollectionSet> profileCollectionSets);
}
