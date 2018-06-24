package com.kairos.persistance.repository.master_data_management.questionnaire_template;


import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomQuestionnaireTemplateRepository {

    List<MasterQuestionnaireTemplateResponseDTO> getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId,Long organizationId);

    MasterQuestionnaireTemplateResponseDTO getMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId, Long organizationId,BigInteger id);
}
