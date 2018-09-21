package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@JaversSpringDataAuditable
public interface QuestionnaireTemplateMongoRepository extends MongoBaseRepository<QuestionnaireTemplate,BigInteger>,CustomQuestionnaireTemplateRepository {

    @Query("{deleted:false,countryId:?0,_id:?1}")
    QuestionnaireTemplate findByIdAndCountryId(Long countryId, BigInteger id);

    QuestionnaireTemplate findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,defaultAssetTemplate:true}")
    QuestionnaireTemplate findDefaultAssetQuestionnaireTemplateByCountryId(Long countryId);

    @Query("{deleted:false,organizationId:?0,defaultAssetTemplate:true}")
    QuestionnaireTemplate findDefaultAssetQuestionnaireTemplateByUnitId(Long unitId);

}
