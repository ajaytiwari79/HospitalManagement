package com.kairos.activity.service.night_worker;

import com.kairos.activity.service.MongoBaseService;
import com.kairos.persistence.model.enums.PersonType;
import com.kairos.response.dto.web.night_worker.NightWorkerGeneralResponseDTO;
import com.kairos.response.dto.web.night_worker.NightWorkerQuestionnaireDTO;
import com.kairos.response.dto.web.night_worker.QuestionnaireAnswerResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
@Service
@Transactional
public class NightWorkerService extends MongoBaseService {

    public List<NightWorkerQuestionnaireDTO> getNightWorkerQuestionnaire(){

        return null;
    }

    public NightWorkerGeneralResponseDTO getNightWorkerDetailsOfStaff(){

        return null;
    }

//    public void updateNightWorkerQuestionnaire

    public List<QuestionnaireAnswerResponseDTO> getQuestionnaireDetailsOfStaff(){

        return null;
    }
}
