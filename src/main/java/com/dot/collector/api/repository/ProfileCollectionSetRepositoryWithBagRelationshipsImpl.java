package com.dot.collector.api.repository;

import com.dot.collector.api.domain.ProfileCollectionSet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ProfileCollectionSetRepositoryWithBagRelationshipsImpl implements ProfileCollectionSetRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PROFILECOLLECTIONSETS_PARAMETER = "profileCollectionSets";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ProfileCollectionSet> fetchBagRelationships(Optional<ProfileCollectionSet> profileCollectionSet) {
        return profileCollectionSet.map(this::fetchSets);
    }

    @Override
    public Page<ProfileCollectionSet> fetchBagRelationships(Page<ProfileCollectionSet> profileCollectionSets) {
        return new PageImpl<>(
            fetchBagRelationships(profileCollectionSets.getContent()),
            profileCollectionSets.getPageable(),
            profileCollectionSets.getTotalElements()
        );
    }

    @Override
    public List<ProfileCollectionSet> fetchBagRelationships(List<ProfileCollectionSet> profileCollectionSets) {
        return Optional.of(profileCollectionSets).map(this::fetchSets).orElse(Collections.emptyList());
    }

    ProfileCollectionSet fetchSets(ProfileCollectionSet result) {
        return entityManager
            .createQuery(
                "select profileCollectionSet from ProfileCollectionSet profileCollectionSet left join fetch profileCollectionSet.sets where profileCollectionSet.id = :id",
                ProfileCollectionSet.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<ProfileCollectionSet> fetchSets(List<ProfileCollectionSet> profileCollectionSets) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, profileCollectionSets.size()).forEach(index -> order.put(profileCollectionSets.get(index).getId(), index));
        List<ProfileCollectionSet> result = entityManager
            .createQuery(
                "select profileCollectionSet from ProfileCollectionSet profileCollectionSet left join fetch profileCollectionSet.sets where profileCollectionSet in :profileCollectionSets",
                ProfileCollectionSet.class
            )
            .setParameter(PROFILECOLLECTIONSETS_PARAMETER, profileCollectionSets)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
