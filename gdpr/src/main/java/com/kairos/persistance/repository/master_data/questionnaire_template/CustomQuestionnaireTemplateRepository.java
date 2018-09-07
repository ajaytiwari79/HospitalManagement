package com.kairos.persistance.repository.master_data.questionnaire_template;


import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomQuestionnaireTemplateRepository {

    MasterQuestionnaireTemplate findByName(Long countryId, Long organizationId, String name);

    List<MasterQuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId,Long organizationId);

    MasterQuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId, Long organizationId,BigInteger templateId);

    MasterQuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsByCountryIdAndId(Long countryId,BigInteger templateId);

    BigInteger getMasterQuestionnaireTemplateIdListByTemplateType(Long countryId, QuestionnaireTemplateType templateType);

}
