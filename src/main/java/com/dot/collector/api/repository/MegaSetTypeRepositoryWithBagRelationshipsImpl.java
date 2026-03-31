package com.dot.collector.api.repository;

import com.dot.collector.api.domain.MegaSetType;
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
public class MegaSetTypeRepositoryWithBagRelationshipsImpl implements MegaSetTypeRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String MEGASETTYPES_PARAMETER = "megaSetTypes";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<MegaSetType> fetchBagRelationships(Optional<MegaSetType> megaSetType) {
        return megaSetType.map(this::fetchAttributes);
    }

    @Override
    public Page<MegaSetType> fetchBagRelationships(Page<MegaSetType> megaSetTypes) {
        return new PageImpl<>(
            fetchBagRelationships(megaSetTypes.getContent()),
            megaSetTypes.getPageable(),
            megaSetTypes.getTotalElements()
        );
    }

    @Override
    public List<MegaSetType> fetchBagRelationships(List<MegaSetType> megaSetTypes) {
        return Optional.of(megaSetTypes).map(this::fetchAttributes).orElse(Collections.emptyList());
    }

    MegaSetType fetchAttributes(MegaSetType result) {
        return entityManager
            .createQuery(
                "select megaSetType from MegaSetType megaSetType left join fetch megaSetType.attributes where megaSetType.id = :id",
                MegaSetType.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<MegaSetType> fetchAttributes(List<MegaSetType> megaSetTypes) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, megaSetTypes.size()).forEach(index -> order.put(megaSetTypes.get(index).getId(), index));
        List<MegaSetType> result = entityManager
            .createQuery(
                "select megaSetType from MegaSetType megaSetType left join fetch megaSetType.attributes where megaSetType in :megaSetTypes",
                MegaSetType.class
            )
            .setParameter(MEGASETTYPES_PARAMETER, megaSetTypes)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
