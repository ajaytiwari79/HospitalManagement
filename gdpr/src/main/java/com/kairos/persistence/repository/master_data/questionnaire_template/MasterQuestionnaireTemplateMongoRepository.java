package com.kairos.persistence.repository.master_data.questionnaire_template;


import com.kairos.persistence.model.master_data.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@JaversSpringDataAuditable
public interface MasterQuestionnaireTemplateMongoRepository extends MongoBaseRepository<MasterQuestionnaireTemplate,BigInteger>,CustomQuestionnaireTemplateRepository {

    @Query("{deleted:false,countryId:?0,_id:?1}")
    MasterQuestionnaireTemplate findByIdAndNonDeleted(Long countryId,BigInteger id);

    MasterQuestionnaireTemplate findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,defaultAssetTemplate:true}")
    MasterQuestionnaireTemplate findDefaultAssetQuestionnaireTemplate(Long countryId);

}
