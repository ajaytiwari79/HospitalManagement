package com.kairos.persistence.repository.questionnaire_template;

import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;

public interface CustomQuestionnaireTemplateRepository {


    QuestionnaireTemplate findPublishedRiskTemplateByAssociatedEntityAndOrgId(Long orgId, QuestionnaireTemplateType riskAssociatedEntity);

    QuestionnaireTemplate findPublishedRiskTemplateByAssetTypeIdAndOrgId(Long orgId, Long assetTypeId);

    QuestionnaireTemplate findPublishedRiskTemplateByOrgIdAndAssetTypeIdAndSubAssetTypeId(Long orgId,Long assetTypeId,Long assetSubTypeId);

    QuestionnaireTemplate findRiskTemplateByAssociatedEntityAndCountryId(Long countryId,  QuestionnaireTemplateType riskAssociatedEntity);

    QuestionnaireTemplate findRiskTemplateByCountryIdAndAssetTypeId(Long countryId ,Long assetTypeId);

    QuestionnaireTemplate findRiskTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeId(Long countryid,Long assetTypeId,Long assetSubTypeId);




}
