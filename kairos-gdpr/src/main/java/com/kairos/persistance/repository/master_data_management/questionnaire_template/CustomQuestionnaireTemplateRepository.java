package com.kairos.persistance.repository.master_data_management.questionnaire_template;


import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateQueryResult;

import java.math.BigInteger;
import java.util.List;

public interface CustomQuestionnaireTemplateRepository {

    List<MasterQuestionnaireTemplateQueryResult> getAllMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId);

    MasterQuestionnaireTemplateQueryResult getMasterQuestionnaireTemplateWithSectionsAndQuestions(Long countryId, BigInteger id);
}
