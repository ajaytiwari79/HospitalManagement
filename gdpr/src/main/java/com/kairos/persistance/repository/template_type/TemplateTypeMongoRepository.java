package com.kairos.persistance.repository.template_type;

import com.kairos.persistance.model.template_type.TemplateType;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * @Auther vikash patwal
 */


@Repository
@JaversSpringDataAuditable
public interface TemplateTypeMongoRepository extends MongoRepository<TemplateType,BigInteger> {


    @Query("{name:?0,deleted:false}")
    TemplateType findByTemplateName(String templateName);

    TemplateType findByid(BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    TemplateType findByTemplateNameAndIsDeleted(Long countryId,String templateName);


    @Query("{deleted:false,name:?0,countryId:?1}")
    TemplateType findByIdAndNameDeleted(String templateName,Long countryId);

    @Query("{deleted:false,_id:?0,countryId:?1}")
    TemplateType findByIdAndNonDeleted(BigInteger id,Long countryId);


    @Query("{_id:{$in:?1},countryId:?0,deleted:false}")
    List<TemplateType> findTemplateTypeByIdsList(Long countryId,List<BigInteger> templateIds);


    @Query("{deleted:false,countryId:?0}")
    List<TemplateType> getAllTemplateType(Long countryId);


}
