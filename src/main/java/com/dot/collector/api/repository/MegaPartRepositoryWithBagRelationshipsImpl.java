package com.dot.collector.api.repository;

import com.dot.collector.api.domain.MegaPart;
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
public class MegaPartRepositoryWithBagRelationshipsImpl implements MegaPartRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String MEGAPARTS_PARAMETER = "megaParts";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<MegaPart> fetchBagRelationships(Optional<MegaPart> megaPart) {
        return megaPart.map(this::fetchPartSubCategories);
    }

    @Override
    public Page<MegaPart> fetchBagRelationships(Page<MegaPart> megaParts) {
        return new PageImpl<>(fetchBagRelationships(megaParts.getContent()), megaParts.getPageable(), megaParts.getTotalElements());
    }

    @Override
    public List<MegaPart> fetchBagRelationships(List<MegaPart> megaParts) {
        return Optional.of(megaParts).map(this::fetchPartSubCategories).orElse(Collections.emptyList());
    }

    MegaPart fetchPartSubCategories(MegaPart result) {
        return entityManager
            .createQuery(
                "select megaPart from MegaPart megaPart left join fetch megaPart.partSubCategories where megaPart.id = :id",
                MegaPart.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<MegaPart> fetchPartSubCategories(List<MegaPart> megaParts) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, megaParts.size()).forEach(index -> order.put(megaParts.get(index).getId(), index));
        List<MegaPart> result = entityManager
            .createQuery(
                "select megaPart from MegaPart megaPart left join fetch megaPart.partSubCategories where megaPart in :megaParts",
                MegaPart.class
            )
            .setParameter(MEGAPARTS_PARAMETER, megaParts)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
