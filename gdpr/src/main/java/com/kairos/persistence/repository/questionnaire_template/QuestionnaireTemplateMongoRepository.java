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


    @Query("{deleted:false,organizationId:?0,templateType:?1,templateStatus:\"PUBLISHED\"}")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByOrganizationIdAndTemplateType(Long countryId, QuestionnaireTemplateType templateType);

    @Query("{countryId:?0,templateType:?1,riskAssociatedEntity:?2,deleted:false}")
    QuestionnaireTemplate findQuestionnaireTemplateByTemplateTypeAndRiskAssociatedEntityAndCountryId(Long countryId, QuestionnaireTemplateType templateType,QuestionnaireTemplateType riskAssociatedEntity);

    @Query("{organizationId:?0,templateType:?1,riskAssociatedEntity:?2,deleted:false,templateStatus:\"PUBLISHED\"}")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByTemplateTypeAndRiskAssociatedEntityAndOrganizationId(Long organizationId, QuestionnaireTemplateType templateType,QuestionnaireTemplateType riskAssociatedEntity);

    @Query("{countryId:?0,templateType:?1,defaultAssetTemplate:?2,deleted:false}")
    QuestionnaireTemplate findQuestionnaireTemplateByTemplateTypeAndDefaultAssetTemplateAndCountryId(Long countryId, QuestionnaireTemplateType templateType,boolean defaultAssetTemplate);

    @Query("{organizationId:?0,templateType:?1,defaultAssetTemplate:?2,deleted:false,templateStatus:\"PUBLISHED\"}")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByTemplateTypeAndDefaultAssetTemplateAndOrganizationId(Long organizationId, QuestionnaireTemplateType templateType,boolean defaultAssetTemplate);

    @Query("{deleted:false,countryId:?0,assetTypeId:?1,assetSubTypeId:{$in:?2},templateType:?3}")
    QuestionnaireTemplate findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndSubAssetTypeByCountryId(Long countryId, BigInteger assetTypeId, List<BigInteger> subAssetTypeIds,QuestionnaireTemplateType templateType);

    @Query("{deleted:false,organizationId:?0,assetTypeId:?1,assetSubTypeId:{$in:?2},templateType:?3,templateStatus:\"PUBLISHED\"}")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByTemplateTypeAndAssetTypeAndSubAssetTypeByOrganizationId(Long organizationId, BigInteger assetTypeId, List<BigInteger> subAssetTypeIds,QuestionnaireTemplateType templateType);

    @Query("{deleted:false,countryId:?0,assetTypeId:?1,assetSubTypeId:{$exists:false},templateType:?2}")
    QuestionnaireTemplate findQuestionnaireTemplateByTemplateTypeAndAssetTypeAndByCountryId(Long countryId, BigInteger assetTypeId,QuestionnaireTemplateType templateType);

    @Query("{deleted:false,organizationId:?0,assetTypeId:?1,assetSubTypeId:{$exists:false},templateType:?2,templateStatus:\"PUBLISHED\"}")
    QuestionnaireTemplate findPublishedQuestionnaireTemplateByTemplateTypeAndAssetTypeAndByOrganizationId(Long organizationId, BigInteger assetTypeId,QuestionnaireTemplateType templateType);

    @Query("{deleted:false,templateType:?0,countryId:?1}")
    QuestionnaireTemplate findQuestionnaireTemplateByCountryIdAndTemplateType( QuestionnaireTemplateType templateType,Long countryId);
}
