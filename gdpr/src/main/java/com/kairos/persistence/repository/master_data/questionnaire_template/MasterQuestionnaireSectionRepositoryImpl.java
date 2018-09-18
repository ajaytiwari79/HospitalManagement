package com.kairos.persistence.repository.master_data.questionnaire_template;

import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireSectionResponseDTO;

import java.math.BigInteger;
import java.util.List;

public class MasterQuestionnaireSectionRepositoryImpl implements CustomMasterQuestionSectionRepository {
    @Override
    public MasterQuestionnaireSectionResponseDTO getMasterQuestionnaireSectionAndQuestion(Long countryId, BigInteger id) {
        return null;
    }

    @Override
    public List<MasterQuestionnaireSectionResponseDTO> getMasterQuestionnaireSectionListWithQuestion(Long countryId) {
        return null;
    }
}
