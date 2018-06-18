package com.kairos.persistance.repository.master_data_management.questionnaire_template;


import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateQueryResult;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomQuestionnaireTemplateRepository {

    List<MasterQuestionnaireTemplateResponseDto> getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId);

    MasterQuestionnaireTemplateResponseDto getMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId, BigInteger id);
}
