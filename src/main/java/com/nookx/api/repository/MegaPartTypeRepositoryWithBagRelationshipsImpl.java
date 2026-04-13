package com.nookx.api.repository;

import com.nookx.api.domain.MegaPartType;
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
public class MegaPartTypeRepositoryWithBagRelationshipsImpl implements MegaPartTypeRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String MEGAPARTTYPES_PARAMETER = "megaPartTypes";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<MegaPartType> fetchBagRelationships(Optional<MegaPartType> megaPartType) {
        return megaPartType.map(this::fetchAttributes);
    }

    @Override
    public Page<MegaPartType> fetchBagRelationships(Page<MegaPartType> megaPartTypes) {
        return new PageImpl<>(
            fetchBagRelationships(megaPartTypes.getContent()),
            megaPartTypes.getPageable(),
            megaPartTypes.getTotalElements()
        );
    }

    @Override
    public List<MegaPartType> fetchBagRelationships(List<MegaPartType> megaPartTypes) {
        return Optional.of(megaPartTypes).map(this::fetchAttributes).orElse(Collections.emptyList());
    }

    MegaPartType fetchAttributes(MegaPartType result) {
        return entityManager
            .createQuery(
                "select megaPartType from MegaPartType megaPartType left join fetch megaPartType.attributes where megaPartType.id = :id",
                MegaPartType.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<MegaPartType> fetchAttributes(List<MegaPartType> megaPartTypes) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, megaPartTypes.size()).forEach(index -> order.put(megaPartTypes.get(index).getId(), index));
        List<MegaPartType> result = entityManager
            .createQuery(
                "select megaPartType from MegaPartType megaPartType left join fetch megaPartType.attributes where megaPartType in :megaPartTypes",
                MegaPartType.class
            )
            .setParameter(MEGAPARTTYPES_PARAMETER, megaPartTypes)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
