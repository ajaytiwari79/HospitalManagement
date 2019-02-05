package com.kairos.persistence.repository.template_type;

import com.kairos.persistence.model.template_type.TemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther vikash patwal
 */


@Repository
//@JaversSpringDataAuditable
public interface TemplateTypeRepository extends JpaRepository<TemplateType,Long> {

    @Query(value = "Select TT from TemplateType TT where TT.id IN (?1) and TT.deleted = false")
    List<TemplateType> findAllById(List<Long> ids);

    @Query(value = "Select TT from TemplateType TT where TT.countryId  = ?1 and TT.deleted = false order by TT.createdAt desc")
    List<TemplateType> getAllTemplateType(Long countryId);

}
