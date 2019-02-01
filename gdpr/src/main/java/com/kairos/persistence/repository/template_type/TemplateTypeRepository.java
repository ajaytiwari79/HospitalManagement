package com.kairos.persistence.repository.template_type;

import com.kairos.persistence.model.template_type.TemplateTypeMD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther vikash patwal
 */


@Repository
//@JaversSpringDataAuditable
public interface TemplateTypeRepository extends JpaRepository<TemplateTypeMD,Long> {

    @Query(value = "Select TT from TemplateTypeMD TT where TT.id IN (?1) and TT.deleted = false")
    List<TemplateTypeMD> findAllById(List<Long> ids);

    @Query(value = "Select TT from TemplateTypeMD TT where TT.countryId  = ?1 and TT.deleted = false order by TT.createdAt desc")
    List<TemplateTypeMD> getAllTemplateType(Long countryId);

}
