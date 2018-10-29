package com.kairos.persistence.repository.questionnaire_template;


import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface CustomQuestionnaireTemplateRepository {

    QuestionnaireTemplate findByNameAndCountryId(Long countryId, String name);

    QuestionnaireTemplate findByNameAndUnitId(Long unitId, String name);

    List<QuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSectionsAndQuestionsByCountryId(Long countryId);

    QuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsByCountryId(Long countryId, BigInteger templateId);

    QuestionnaireTemplate findQuestionnaireTemplateOfTemplateTypeRiskAndAsssociatedEntityProcessingActivityByCountryId(Long countryId);

    QuestionnaireTemplate findQuestionnaireTemplateOfTemplateTypeRiskByCountryIdAndAssetTypeId(Long countryId, BigInteger assetTypeId);

    QuestionnaireTemplate findQuestionnaireTemplateOfTemplateTypeRiskByCountryIdAndAssetTypeIdAndSubAssetTypeId(Long countryId, BigInteger assetTypeId, BigInteger subAssetTypeId);

    QuestionnaireTemplate getQuestionnaireTemplateByTemplateTypeByUnitId(Long unitId, QuestionnaireTemplateType templateType);

    QuestionnaireTemplateResponseDTO getQuestionnaireTemplateWithSectionsByUnitId(Long unitId, BigInteger templateId);

    List<QuestionnaireTemplateResponseDTO> getAllQuestionnaireTemplateWithSectionsAndQuestionsByUnitId(Long unitId);

    QuestionnaireTemplate findDefaultAssetQuestionnaireTemplateByUnitId(Long unitId);

    QuestionnaireTemplate findPublishedQuestionnaireTemplateByAssetTypeAndSubAssetTypeByUnitId(Long unitId, BigInteger assetTypeId, List<BigInteger> subAssetTypeIds);

    QuestionnaireTemplate findPublishedQuestionnaireTemplateByAssetTypeAndByUnitId(Long unitId, BigInteger assetTypeId);

    QuestionnaireTemplate findPublishedQuestionnaireTemplateByUnitIdAndTemplateType(Long unitId, QuestionnaireTemplateType templateType);

    QuestionnaireTemplate findPublishedQuestionnaireTemplateOfTemplateTypeRiskAndAssociatedEntityProcessingActivityByUnitId(Long unitId);

    QuestionnaireTemplate findPublishedTemplateOfTemplateTypeRiskByUnitIdAndAssetTypeId(Long unitId, BigInteger assetTypeId);

    QuestionnaireTemplate findPublishedTemplateOfTemplateTypeRiskByUnitIdAndAssetTypeIdAndSubAssetTypeId(Long unitId, BigInteger assetTypeId, BigInteger assetSubTypeId);


}
