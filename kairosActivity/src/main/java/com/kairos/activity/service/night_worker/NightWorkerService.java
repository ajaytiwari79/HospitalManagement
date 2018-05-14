package com.kairos.activity.service.night_worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.night_worker.NightWorker;
import com.kairos.activity.persistence.model.night_worker.QuestionAnswerPair;
import com.kairos.activity.persistence.model.night_worker.StaffQuestionnaire;
import com.kairos.activity.persistence.repository.night_worker.NightWorkerMongoRepository;
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

/**
 * Created by prerna on 8/5/18.
 */
@Service
@Transactional
public class NightWorkerService extends MongoBaseService {

    @Inject
    NightWorkerMongoRepository nightWorkerMongoRepository;

    public List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaire(Long unitId, Long staffId){
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        /*if(!Optional.ofNullable(nightWorker).isPresent()){
            throw new DataNotFoundByIdException("Staff is not a night worker");
        }*/
        List<QuestionnaireAnswerResponseDTO> questionnaireAnswerResponseDTOS = new ArrayList<QuestionnaireAnswerResponseDTO>();
        questionnaireAnswerResponseDTOS.addAll(nightWorkerMongoRepository.getNightWorkerQuestionnaireDetails(staffId) );
        boolean nightWorkerQuestionnaireFormIsEnabled = true;
        if(Optional.ofNullable(nightWorker).isPresent() && nightWorkerQuestionnaireFormIsEnabled){
            boolean temp = checkIfNightWorkerQuestionnaireFormIsEnabled(staffId, nightWorker.getQuestionnaireFrequencyInMonths());

            List<QuestionAnswerDTO>  enabledQuestionnaireFormData = nightWorkerMongoRepository.getNightWorkerQuestions();
            questionnaireAnswerResponseDTOS.add(new QuestionnaireAnswerResponseDTO("Questionnaire", true, enabledQuestionnaireFormData));
        }
        return questionnaireAnswerResponseDTOS;
    }

    public NightWorkerGeneralResponseDTO getNightWorkerDetailsOfStaff(Long unitId, Long staffId){
        // TODO check if valid staff
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            ObjectMapper objectMapper =  new ObjectMapper();
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
        // TODO check if valid staff

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
        }
        save(nightWorker);
        return ObjectMapperUtils.copyPropertiesByMapper(nightWorker, NightWorkerGeneralResponseDTO.class);
    }

    public boolean checkIfNightWorkerQuestionnaireFormIsEnabled(Long staffId, int questionnaireFrequencyInMonths){
        Date lastApplicableDateForQuestionnaire = DateUtils.addMonths(DateUtils.getDate(), questionnaireFrequencyInMonths);
        return nightWorkerMongoRepository.checkIfNightWorkerQuestionnaireFormIsEnabled(staffId, lastApplicableDateForQuestionnaire);
    }

    public String prepareNameOfQuestionnaireSet(int questionnaireCount){
        return "Questionnaire "+(questionnaireCount+1);
    }

    public QuestionnaireAnswerResponseDTO addNightWorkerQuestionnaire(Long unitId, Long staffId, QuestionnaireAnswerResponseDTO answerResponseDTO){
        // TODO check if valid staff
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if(!Optional.ofNullable(nightWorker).isPresent()){
            throw new DataNotFoundByIdException("Staff is not a night worker");
        }
//        List<QuestionAnswerPair> tempQAPair = ObjectMapperUtils.copyProperties(answerResponseDTO.getQuestionAnswerPair(), QuestionAnswerPair.class);
        // TODO Validate if questionnaire is being added after valid frequency
       /* if(!checkIfNightWorkerQuestionnaireFormIsEnabled(staffId, nightWorker.getQuestionnaireFrequencyInMonths())){
            throw new DataNotFoundByIdException("Questionnaire for night worker is not enabled yet");
        }*/

        StaffQuestionnaire staffQuestionnaire = new StaffQuestionnaire(
                prepareNameOfQuestionnaireSet(Optional.ofNullable(nightWorker.getStaffQuestionnairesId()).isPresent() ? nightWorker.getStaffQuestionnairesId().size() : 0),
                ObjectMapperUtils.copyPropertiesOfListByMapper(answerResponseDTO.getQuestionAnswerPair(), QuestionAnswerPair.class));
        save(staffQuestionnaire);
        if(Optional.ofNullable(nightWorker.getStaffQuestionnairesId()).isPresent()){
            nightWorker.getStaffQuestionnairesId().add(staffQuestionnaire.getId());
        } else {
            nightWorker.setStaffQuestionnairesId(new ArrayList<BigInteger>() {{
                add(staffQuestionnaire.getId());
            }});
        }
        save(nightWorker);
        answerResponseDTO.setId(staffQuestionnaire.getId());
        answerResponseDTO.setName(staffQuestionnaire.getName());
        return answerResponseDTO;
    }

}
