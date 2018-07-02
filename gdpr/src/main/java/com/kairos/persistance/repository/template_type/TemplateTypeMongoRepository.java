package com.kairos.persistance.repository.template_type;

import com.kairos.persistance.model.template_type.TemplateType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * @Auther vikash patwal
 */

public interface TemplateTypeMongoRepository extends MongoRepository<TemplateType,BigInteger> {


    @Query("{templateName:?0,deleted:false}")
    TemplateType findByTemplateName(String templateName);

    TemplateType findByid(BigInteger id);

    @Query("{countryId:?0,templateName:?1,deleted:false}")
    TemplateType findByTemplateNameAndIsDeleted(Long countryId,String templateName);


    @Query("{deleted:false,templateName:?0,countryId:?1}")
    TemplateType findByIdAndNameDeleted(String templateName,Long countryId);

    @Query("{deleted:false,_id:?0,countryId:?1}")
    TemplateType findByIdAndNonDeleted(BigInteger id,Long countryId);


    @Query("{templateName:{$in:?0},deleted:false}")
    List<TemplateType> findByNameList(Set<String> templateName);


    @Query("{deleted:false,countryId:?0}")
    List<TemplateType> getAllTemplateType(Long countryId);


}
