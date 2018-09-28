package com.kairos.persistence.repository.questionnaire_template;

import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireSectionResponseDTO;

import java.math.BigInteger;
import java.util.List;

public class QuestionnaireSectionRepositoryImpl implements CustomQuestionSectionRepository {
    @Override
    public QuestionnaireSectionResponseDTO getMasterQuestionnaireSectionAndQuestion(Long countryId, BigInteger id) {
        return null;
    }

    @Override
    public List<QuestionnaireSectionResponseDTO> getMasterQuestionnaireSectionListWithQuestion(Long countryId) {
        return null;
    }
}
