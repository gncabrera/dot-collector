package com.nookx.api.repository;

import com.nookx.api.domain.MegaSetType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaSetType entity.
 *
 * When extending this class, extend MegaSetTypeRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface MegaSetTypeRepository extends MegaSetTypeRepositoryWithBagRelationships, JpaRepository<MegaSetType, Long> {
    default Optional<MegaSetType> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<MegaSetType> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<MegaSetType> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    @Query("select distinct t from MegaSetType t left join fetch t.attributes where t.name = :name and t.isLatest = true")
    Optional<MegaSetType> findLatestByName(@Param("name") String name);

    @Query("select distinct t from MegaSetType t left join fetch t.attributes where t.isLatest = true")
    List<MegaSetType> findAllLatestWithAttributes();

    @Query(
        "select distinct t from MegaSetType t left join fetch t.attributes where " +
            "(:name is null or t.name = :name) and " +
            "(:isLatest is null or t.isLatest = :isLatest) and " +
            "(:active is null or t.active = :active)"
    )
    List<MegaSetType> search(@Param("name") String name, @Param("isLatest") Boolean isLatest, @Param("active") Boolean active);

    List<MegaSetType> findByNameOrderByVersionDesc(String name);

    Optional<MegaSetType> findFirstByNameOrderByVersionDesc(String name);

    boolean existsByName(String name);
}
