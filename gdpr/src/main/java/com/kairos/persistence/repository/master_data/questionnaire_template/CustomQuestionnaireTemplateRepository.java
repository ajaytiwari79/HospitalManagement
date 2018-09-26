package com.kairos.persistence.repository.master_data.questionnaire_template;


import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistence.model.master_data.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomQuestionnaireTemplateRepository {

    MasterQuestionnaireTemplate findByName(Long countryId, String name);

    List<MasterQuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId);

    MasterQuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId, BigInteger templateId);

    MasterQuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsByCountryIdAndId(Long countryId,BigInteger templateId);

    BigInteger getMasterQuestionnaireTemplateIdListByTemplateType(Long countryId, QuestionnaireTemplateType templateType);

}
