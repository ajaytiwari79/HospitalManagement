package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateType;
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

    @Query("{deleted:false,countryId:?0,assetType:?1,assetSubType:{$in:?2}}")
    QuestionnaireTemplate findQuestionnaireTemplateByAssetTypeAndSubAssetTypeByCountryId(Long countryId, BigInteger assetTypeId, List<BigInteger> subAssetTypeIds);

    @Query("{deleted:false,countryId:?0,assetType:?1,assetSubType:{$exists:false}}")
    QuestionnaireTemplate findQuestionnaireTemplateByAssetTypeAndByCountryId(Long countryId, BigInteger assetTypeId);

    @Query("{deleted:false,organizationId:?0,templateType:?1}")
    QuestionnaireTemplate findQuestionnaireTemplateByCountryIdAndTemplateType(Long countryId, QuestionnaireTemplateType templateType);


}
