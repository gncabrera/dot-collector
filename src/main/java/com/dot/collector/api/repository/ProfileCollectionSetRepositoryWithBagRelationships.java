package com.dot.collector.api.repository;

import com.dot.collector.api.domain.ProfileCollectionSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ProfileCollectionSetRepositoryWithBagRelationships {
    Optional<ProfileCollectionSet> fetchBagRelationships(Optional<ProfileCollectionSet> profileCollectionSet);

    List<ProfileCollectionSet> fetchBagRelationships(List<ProfileCollectionSet> profileCollectionSets);

    Page<ProfileCollectionSet> fetchBagRelationships(Page<ProfileCollectionSet> profileCollectionSets);
}
