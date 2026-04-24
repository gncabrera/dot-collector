package com.nookx.api.repository;

import com.nookx.api.domain.ProfileCollectionSet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileCollectionSet entity.
 *
 * When extending this class, extend ProfileCollectionSetRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ProfileCollectionSetRepository
    extends ProfileCollectionSetRepositoryWithBagRelationships, JpaRepository<ProfileCollectionSet, Long>
{
    List<ProfileCollectionSet> findByCollection_Id(Long collectionId);

    default Optional<ProfileCollectionSet> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<ProfileCollectionSet> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<ProfileCollectionSet> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
