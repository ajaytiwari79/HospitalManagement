package com.kairos.persistence.repository.template_type;

import com.kairos.persistence.model.template_type.TemplateTypeMD;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther vikash patwal
 */


@Repository
@JaversSpringDataAuditable
public interface TemplateTypeRepository extends JpaRepository<TemplateTypeMD,Long> {

    @Query(value = "Select TT from TemplateTypeMD TT where TT.id IN (?1) and TT.deleted = false")
    List<TemplateTypeMD> findAllById(List<Long> ids);

    @Query(value = "Select TT from TemplateTypeMD TT where TT.countryId  = ?1 and TT.deleted = false order by TT.createdAt desc")
    List<TemplateTypeMD> getAllTemplateType(Long countryId);


    /*@Query("{name:?0,deleted:false}")
    TemplateType findByTemplateName(String templateName);

    TemplateType findByid(BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    TemplateType findByTemplateNameAndIsDeleted(Long countryId, String templateName);


    @Query("{deleted:false,name:?0,countryId:?1}")
    TemplateType findByIdAndNameDeleted(String templateName, Long countryId);

    @Query("{deleted:false,_id:?0,countryId:?1}")
    TemplateType findByIdAndNonDeleted(BigInteger id, Long countryId);


    @Query("{_id:{$in:?1},countryId:?0,deleted:false}")
    List<TemplateType> findTemplateTypeByIdsList(Long countryId, List<BigInteger> templateIds);


    @Query("{deleted:false,countryId:?0}")
    List<TemplateType> getAllTemplateType(Long countryId, Sort sort);*/


}
