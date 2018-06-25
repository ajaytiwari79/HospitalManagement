package com.kairos.persistance.repository.master_data_management.questionnaire_template;

import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireSectionResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterQuestionSectionRepository {

    MasterQuestionnaireSectionResponseDTO getMasterQuestionnaireSectionAndQuestion(Long countryId, BigInteger id);

    List<MasterQuestionnaireSectionResponseDTO> getMasterQuestionnaireSectionListWithQuestion(Long countryId);

}
