package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomQuestionnaireTemplateRepository {

    QuestionnaireTemplate findByNameAndCountryId(Long countryId, String name);

    QuestionnaireTemplate findByNameAndUnitId(Long unitId, String name);

    List<QuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSectionsAndQuestionsByCountryId(Long countryId);

    QuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsByCountryId(Long countryId, BigInteger templateId);

    QuestionnaireTemplate findRiskTemplateByAssociatedProcessingActivityAndCountryId(Long countryId);

    QuestionnaireTemplate findRiskTemplateByCountryIdAndAssetTypeId(Long countryId, BigInteger assetTypeId);

    QuestionnaireTemplate findRiskTemplateByCountryIdAndAssetTypeIdAndSubAssetTypeId(Long countryId, BigInteger assetTypeId, BigInteger subAssetTypeId);

    QuestionnaireTemplate getQuestionnaireTemplateByTemplateTypeAndUnitId(Long unitId, QuestionnaireTemplateType templateType);

    QuestionnaireTemplateResponseDTO getQuestionnaireTemplateWithSectionsByUnitId(Long unitId, BigInteger templateId);

    List<QuestionnaireTemplateResponseDTO> getAllQuestionnaireTemplateWithSectionsAndQuestionsByUnitId(Long unitId);

    QuestionnaireTemplate findDefaultAssetQuestionnaireTemplateByUnitId(Long unitId);

    QuestionnaireTemplate findPublishedQuestionnaireTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeId(Long unitId, BigInteger assetTypeId, BigInteger subAssetTypeId);

    QuestionnaireTemplate findPublishedQuestionnaireTemplateByAssetTypeAndByUnitId(Long unitId, BigInteger assetTypeId);

    QuestionnaireTemplate findPublishedQuestionnaireTemplateByUnitIdAndTemplateType(Long unitId, QuestionnaireTemplateType templateType);

    QuestionnaireTemplate findPublishedRiskTemplateByAssociatedProcessingActivityAndUnitId(Long unitId);

    QuestionnaireTemplate findPublishedRiskTemplateByUnitIdAndAssetTypeId(Long unitId, BigInteger assetTypeId);

    QuestionnaireTemplate findPublishedRiskTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeId(Long unitId, BigInteger assetTypeId, BigInteger assetSubTypeId);


}
