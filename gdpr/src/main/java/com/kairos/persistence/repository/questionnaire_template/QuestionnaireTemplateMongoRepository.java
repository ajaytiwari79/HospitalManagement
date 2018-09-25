package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface QuestionnaireTemplateMongoRepository extends MongoBaseRepository<QuestionnaireTemplate, BigInteger>, CustomQuestionnaireTemplateRepository {

    @Query("{deleted:false,countryId:?0,_id:?1}")
    QuestionnaireTemplate findByCountryIdAndId(Long countryId, BigInteger questionnaireTemplateId);

    @Query("{deleted:false,organizationId:?0,_id:?1}")
    QuestionnaireTemplate findByUnitIdAndId(Long countryId, BigInteger questionnaireTemplateId);

    QuestionnaireTemplate findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,defaultAssetTemplate:true}")
    QuestionnaireTemplate findDefaultAssetQuestionnaireTemplateByCountryId(Long countryId);

    @Query("{deleted:false,organizationId:?0,defaultAssetTemplate:true}")
    QuestionnaireTemplate findDefaultAssetQuestionnaireTemplateByUnitId(Long unitId);

    @Query("{deleted:false,organizationId:?0,assetType:?1.assetSubType:{$in:?2}}")
    QuestionnaireTemplate findQuestionnaireTemplateByAssetTypeAndSubAssetType(Long unitId, BigInteger assetTypeId, List<BigInteger> subAssetTypeIds);


}
