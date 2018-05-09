package com.kairos.activity.service.night_worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.activity.persistence.model.night_worker.NightWorker;
import com.kairos.activity.persistence.model.night_worker.QuestionAnswerPair;
import com.kairos.activity.persistence.model.night_worker.StaffQuestionnaire;
import com.kairos.activity.persistence.repository.night_worker.NightWorkerMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.persistence.model.enums.PersonType;
import com.kairos.response.dto.web.night_worker.NightWorkerGeneralResponseDTO;
import com.kairos.response.dto.web.night_worker.NightWorkerQuestionnaireDTO;
import com.kairos.response.dto.web.night_worker.QuestionnaireAnswerResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created by prerna on 8/5/18.
 */
@Service
@Transactional
public class NightWorkerService extends MongoBaseService {

    @Inject
    NightWorkerMongoRepository nightWorkerMongoRepository;

    public List<NightWorkerQuestionnaireDTO> getNightWorkerQuestionnaire(){

        return null;
    }

    public NightWorkerGeneralResponseDTO getNightWorkerDetailsOfStaff(Long unitId, Long staffId){
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            ObjectMapper objectMapper =  new ObjectMapper();
            return objectMapper.convertValue(nightWorker, NightWorkerGeneralResponseDTO.class);
        } else {
            return new NightWorkerGeneralResponseDTO(false);
        }
    }

    public NightWorkerGeneralResponseDTO updateNightWorkerGeneralDetails(Long unitId, Long staffId, NightWorkerGeneralResponseDTO nightWorkerDTO){
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            nightWorker.setNightWorker(nightWorkerDTO.isNightWorker());
            nightWorker.setStartDate(nightWorkerDTO.getStartDate());
            nightWorker.setPersonType(nightWorkerDTO.getPersonType());
            nightWorker.setQuestionnaireFrequency(nightWorkerDTO.getQuestionnaireFrequency());
        } else {
            nightWorker = new NightWorker(nightWorkerDTO.isNightWorker(), nightWorkerDTO.getStartDate(), nightWorkerDTO.getPersonType(), nightWorkerDTO.getQuestionnaireFrequency());
        }
        save(nightWorker);
        ObjectMapper objectMapper =  new ObjectMapper();
        return objectMapper.convertValue(nightWorker, NightWorkerGeneralResponseDTO.class);
    }

    public StaffQuestionnaire addNightWorkerQuestionnaire(Long unitId, Long staffId, QuestionnaireAnswerResponseDTO answerResponseDTO){
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        StaffQuestionnaire staffQuestionnaire = new StaffQuestionnaire(
                ObjectMapperUtils.copyProperties(answerResponseDTO.getQuestionAnswerPair(), QuestionAnswerPair.class));
        save(staffQuestionnaire);
        nightWorker.getStaffQuestionnaires().add(staffQuestionnaire.getId());
        save(nightWorker);
        return staffQuestionnaire;
    }

    /*public List<QuestionnaireAnswerResponseDTO> updateNightWorkerGeneralDetails(Long staffId, QuestionnaireAnswerResponseDTO questionnaireDTO){
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            nightWorker.setNightWorker(nightWorkerDTO.isNightWorker());
            nightWorker.setStartDate(nightWorkerDTO.getStartDate());
            nightWorker.setPersonType(nightWorkerDTO.getPersonType());
            nightWorker.setQuestionnaireFrequency(nightWorkerDTO.getQuestionnaireFrequency());
        } else {
            nightWorker = new NightWorker(nightWorkerDTO.isNightWorker(), nightWorkerDTO.getStartDate(), nightWorkerDTO.getPersonType(), nightWorkerDTO.getQuestionnaireFrequency());
        }
        save(nightWorker);
        ObjectMapper objectMapper =  new ObjectMapper();
        return objectMapper.convertValue(nightWorker, NightWorkerGeneralResponseDTO.class);
    }*/

//    public void updateNightWorkerQuestionnaire

    public List<QuestionnaireAnswerResponseDTO> getQuestionnaireDetailsOfStaff(){

        return null;
    }
}
