package com.kairos.persistance.repository.template_type;

import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.model.master_data_management.asset_management.DataDisposal;
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


    @Query("{templateName:?0}")
    TemplateType findByTemplateName(String templateName);


    @Query("{templateName:?0,deleted:false}")
    TemplateType findByTemplateNameAndIsDeleted(String templateName);


    @Query("{deleted:false,templateName:?0}")
    TemplateType findByIdAndNameDeleted(String templateName);

    @Query("{deleted:false,_id:?0}")
    TemplateType findByIdAndNonDeleted(BigInteger id);


    @Query("{templateName:{$in:?0},deleted:false}")
    List<TemplateType> findByNameList(Set<String> templateName);


}
