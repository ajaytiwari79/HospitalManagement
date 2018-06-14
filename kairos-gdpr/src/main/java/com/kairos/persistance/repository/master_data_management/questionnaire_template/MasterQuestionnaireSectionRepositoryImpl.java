package com.kairos.persistance.repository.master_data_management.questionnaire_template;

import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireSectionResponseDto;

import java.math.BigInteger;
import java.util.List;

public class MasterQuestionnaireSectionRepositoryImpl implements CustomMasterQuestionSectionRepository {
    @Override
    public MasterQuestionnaireSectionResponseDto getMasterQuestionnaireSectionAndQuestion(Long countryId, BigInteger id) {
        return null;
    }

    @Override
    public List<MasterQuestionnaireSectionResponseDto> getMasterQuestionnaireSectionListWithQuestion(Long countryId) {
        return null;
    }
}
