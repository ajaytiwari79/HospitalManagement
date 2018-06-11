package com.kairos.persistance.repository.master_data_management.questionnaire_template;

import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireSectionResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterQuestionSectionRepository {

    MasterQuestionnaireSectionResponseDto getMasterQuestionnaireSectionAndQuestion(Long countryId, BigInteger id);

    List<MasterQuestionnaireSectionResponseDto> getMasterQuestionnaireSectionListWithQuestion(Long countryId);

}
