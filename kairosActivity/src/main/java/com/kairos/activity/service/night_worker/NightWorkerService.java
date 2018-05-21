package com.kairos.activity.service.night_worker;

import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.persistence.model.night_worker.NightWorker;
import com.kairos.activity.persistence.model.night_worker.NightWorkerUnitSettings;
import com.kairos.activity.persistence.model.night_worker.QuestionAnswerPair;
import com.kairos.activity.persistence.model.night_worker.StaffQuestionnaire;
import com.kairos.activity.persistence.repository.night_worker.NightWorkerMongoRepository;
import com.kairos.activity.persistence.repository.night_worker.NightWorkerUnitSettingsMongoRepository;
import com.kairos.activity.persistence.repository.night_worker.StaffQuestionnaireMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.night_worker.NightWorkerGeneralResponseDTO;
import com.kairos.response.dto.web.night_worker.NightWorkerUnitSettingsDTO;
import com.kairos.response.dto.web.night_worker.QuestionAnswerDTO;
import com.kairos.response.dto.web.night_worker.QuestionnaireAnswerResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
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
    ExceptionService exceptionService;

    @Inject
    StaffQuestionnaireMongoRepository staffQuestionnaireMongoRepository;

    @Inject
    NightWorkerUnitSettingsMongoRepository nightWorkerUnitSettingsMongoRepository;

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
            exceptionService.dataNotFoundByIdException("message.questionnaire.frequency");
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
            answerResponseDTO.setSubmittedOn(staffQuestionnaire.getSubmittedOn());
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

    public NightWorkerUnitSettings createDefaultNightWorkerSettings(Long unitId) {
        NightWorkerUnitSettings nightWorkerSettings = new NightWorkerUnitSettings(AppConstants.ELIGIBLE_MIN_AGE,AppConstants.ELIGIBLE_MAX_AGE, unitId);
        save(nightWorkerSettings);
        return nightWorkerSettings;
    }

    public NightWorkerUnitSettingsDTO getNightWorkerSettings(Long unitId){
        NightWorkerUnitSettings nightWorkerSettings = nightWorkerUnitSettingsMongoRepository.findByUnit(unitId);
        if(!Optional.ofNullable(nightWorkerSettings).isPresent()){
            nightWorkerSettings =  createDefaultNightWorkerSettings(unitId);
        }
        return ObjectMapperUtils.copyPropertiesByMapper(nightWorkerSettings, NightWorkerUnitSettingsDTO.class);
    }

    public NightWorkerUnitSettingsDTO updateNightWorkerSettings(Long unitId, NightWorkerUnitSettingsDTO unitSettingsDTO) {
        NightWorkerUnitSettings nightWorkerSettings = nightWorkerUnitSettingsMongoRepository.findByUnit(unitId);
        if (!Optional.ofNullable(nightWorkerSettings).isPresent()) {
            throw new DataNotFoundByIdException("Night Worker setting not found for unit : "+unitId);
        }
        nightWorkerSettings.setEligibleMinAge(unitSettingsDTO.getEligibleMinAge());
        nightWorkerSettings.setEligibleMaxAge(unitSettingsDTO.getEligibleMaxAge());

        save(nightWorkerSettings);
        return unitSettingsDTO;
    }
}
