package com.kairos.persistence.repository.questionnaire_template;

import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireSectionResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomQuestionSectionRepository {

    QuestionnaireSectionResponseDTO getMasterQuestionnaireSectionAndQuestion(Long countryId, BigInteger id);

    List<QuestionnaireSectionResponseDTO> getMasterQuestionnaireSectionListWithQuestion(Long countryId);

}
