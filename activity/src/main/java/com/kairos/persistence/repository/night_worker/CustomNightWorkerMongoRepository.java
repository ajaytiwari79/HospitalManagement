package com.kairos.persistence.repository.night_worker;

import com.kairos.dto.activity.night_worker.QuestionAnswerDTO;
import com.kairos.dto.activity.night_worker.QuestionnaireAnswerResponseDTO;

import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
public interface CustomNightWorkerMongoRepository {

    List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaireDetails(Long staffId);

    List<QuestionAnswerDTO> getNightWorkerQuestions();
}
