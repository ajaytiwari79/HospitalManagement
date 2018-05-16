package com.kairos.activity.service.night_worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.night_worker.NightWorker;
import com.kairos.activity.persistence.model.night_worker.QuestionAnswerPair;
import com.kairos.activity.persistence.model.night_worker.StaffQuestionnaire;
import com.kairos.activity.persistence.repository.night_worker.NightWorkerMongoRepository;
import com.kairos.activity.persistence.repository.night_worker.StaffQuestionnaireMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.night_worker.NightWorkerGeneralResponseDTO;
import com.kairos.response.dto.web.night_worker.QuestionAnswerDTO;
import com.kairos.response.dto.web.night_worker.QuestionnaireAnswerResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by prerna on 8/5/18.
 */
@Service
@Transactional
public class NightWorkerService extends MongoBaseService {

    @Inject
    NightWorkerMongoRepository nightWorkerMongoRepository;

    @Inject
    StaffQuestionnaireMongoRepository staffQuestionnaireMongoRepository;

    public List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaire(Long unitId, Long staffId){
        return nightWorkerMongoRepository.getNightWorkerQuestionnaireDetails(staffId);
    }

    public NightWorkerGeneralResponseDTO getNightWorkerDetailsOfStaff(Long unitId, Long staffId){

        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            return ObjectMapperUtils.copyPropertiesByMapper(nightWorker, NightWorkerGeneralResponseDTO.class);
        } else {
            return new NightWorkerGeneralResponseDTO(false);
        }
    }

    public void validateNightWorkerGeneralDetails(NightWorkerGeneralResponseDTO nightWorkerDTO){
        if(nightWorkerDTO.getQuestionnaireFrequencyInMonths() <= 0){
            throw new DataNotFoundByIdException("Invalid questionnaire frequency.");
        }
    }

    public NightWorkerGeneralResponseDTO updateNightWorkerGeneralDetails(Long unitId, Long staffId, NightWorkerGeneralResponseDTO nightWorkerDTO){

        validateNightWorkerGeneralDetails(nightWorkerDTO);
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            nightWorker.setNightWorker(nightWorkerDTO.isNightWorker());
            nightWorker.setStartDate(nightWorkerDTO.getStartDate());
            nightWorker.setPersonType(nightWorkerDTO.getPersonType());
            nightWorker.setQuestionnaireFrequencyInMonths(nightWorkerDTO.getQuestionnaireFrequencyInMonths());
        } else if (!nightWorkerDTO.isNightWorker()){
            return new NightWorkerGeneralResponseDTO(false);
        } else {
            nightWorker = new NightWorker(nightWorkerDTO.isNightWorker(), nightWorkerDTO.getStartDate(), nightWorkerDTO.getPersonType(), nightWorkerDTO.getQuestionnaireFrequencyInMonths(), staffId);
            StaffQuestionnaire staffQuestionnaire = addDefaultStaffQuestionnaire();
            nightWorker.setStaffQuestionnairesId(new ArrayList<BigInteger>() {{
                add(staffQuestionnaire.getId());
            }});
        }
        save(nightWorker);
        return ObjectMapperUtils.copyPropertiesByMapper(nightWorker, NightWorkerGeneralResponseDTO.class);
    }

    public String prepareNameOfQuestionnaireSet(){
        return AppConstants.QUESTIONNAIE_NAME_PREFIX + " " + DateUtils.getDateString(DateUtils.getDate(), "dd_MMM_yyyy");
    }

    public QuestionnaireAnswerResponseDTO updateNightWorkerQuestionnaire(Long unitId, Long staffId, BigInteger questionnaireId, QuestionnaireAnswerResponseDTO answerResponseDTO){

        StaffQuestionnaire staffQuestionnaire = staffQuestionnaireMongoRepository.findByIdAndDeleted(questionnaireId);

        // Predicate to check if any question is unanswere ( null)
        Predicate<QuestionAnswerDTO> predicate = s -> !Optional.ofNullable(s.getAnswer()).isPresent();
        if(!staffQuestionnaire.isSubmitted() && ! (answerResponseDTO.getQuestionAnswerPair().stream().anyMatch(predicate)) ){
            staffQuestionnaire.setSubmitted(true);
            staffQuestionnaire.setSubmittedOn(DateUtils.getLocalDateFromDate(DateUtils.getDate()));
            answerResponseDTO.setSubmitted(true);
        }
        staffQuestionnaire.setQuestionAnswerPair(ObjectMapperUtils.copyPropertiesOfListByMapper(answerResponseDTO.getQuestionAnswerPair(), QuestionAnswerPair.class));
        save(staffQuestionnaire);
        return answerResponseDTO;
    }

    public StaffQuestionnaire addDefaultStaffQuestionnaire(){
        List<QuestionAnswerDTO>  questionnaire = nightWorkerMongoRepository.getNightWorkerQuestions();
        StaffQuestionnaire staffQuestionnaire = new StaffQuestionnaire(
                prepareNameOfQuestionnaireSet(),
                ObjectMapperUtils.copyPropertiesOfListByMapper(questionnaire, QuestionAnswerPair.class));
        save(staffQuestionnaire);
        return staffQuestionnaire;
    }

    // Function will called for scheduled job
    public void createNightWorkerQuestionnaireForStaff(Long staffId){

        StaffQuestionnaire staffQuestionnaire = addDefaultStaffQuestionnaire();
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);

        if(Optional.ofNullable(nightWorker.getStaffQuestionnairesId()).isPresent()){
            nightWorker.getStaffQuestionnairesId().add(staffQuestionnaire.getId());
        } else {
            nightWorker.setStaffQuestionnairesId(new ArrayList<BigInteger>() {{
                add(staffQuestionnaire.getId());
            }});
        }
        save(nightWorker);
    }


}
