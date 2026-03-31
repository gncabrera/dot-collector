package com.dot.collector.api.repository;

import com.dot.collector.api.domain.MegaPartType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaPartType entity.
 *
 * When extending this class, extend MegaPartTypeRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface MegaPartTypeRepository extends MegaPartTypeRepositoryWithBagRelationships, JpaRepository<MegaPartType, Long> {
    default Optional<MegaPartType> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<MegaPartType> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<MegaPartType> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
