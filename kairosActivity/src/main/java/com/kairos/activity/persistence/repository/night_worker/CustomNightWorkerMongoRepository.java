package com.kairos.activity.persistence.repository.night_worker;

import com.kairos.response.dto.web.night_worker.QuestionAnswerDTO;
import com.kairos.response.dto.web.night_worker.QuestionnaireAnswerResponseDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
public interface CustomNightWorkerMongoRepository {

    List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaireDetails(Long staffId);

    List<QuestionAnswerDTO> getNightWorkerQuestions();
}
