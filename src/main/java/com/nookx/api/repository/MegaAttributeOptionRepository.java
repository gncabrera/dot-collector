package com.nookx.api.repository;

import com.nookx.api.domain.MegaAttributeOption;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MegaAttributeOption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MegaAttributeOptionRepository extends JpaRepository<MegaAttributeOption, Long> {
    List<MegaAttributeOption> findByAttribute_Id(Long attributeId);
}
