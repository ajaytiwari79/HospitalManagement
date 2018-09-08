package com.kairos.service.night_worker;

import com.kairos.dto.activity.night_worker.NightWorkerGeneralResponseDTO;
import com.kairos.dto.activity.night_worker.QuestionAnswerDTO;
import com.kairos.dto.activity.night_worker.QuestionnaireAnswerResponseDTO;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.night_worker.NightWorker;
import com.kairos.persistence.model.night_worker.QuestionAnswerPair;
import com.kairos.persistence.model.night_worker.StaffQuestionnaire;
import com.kairos.persistence.model.unit_settings.UnitAgeSetting;
import com.kairos.persistence.repository.night_worker.NightWorkerMongoRepository;
import com.kairos.persistence.repository.night_worker.StaffQuestionnaireMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitAgeSettingMongoRepository;
import com.kairos.rest_client.StaffRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.night_worker.NightWorkerAgeEligibilitySpecification;
import com.kairos.rule_validator.night_worker.StaffNonPregnancySpecification;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.dto.user.staff.staff.UnitStaffResponseDTO;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by prerna on 8/5/18.
 */
@Service
@Transactional
public class NightWorkerService extends MongoBaseService {

    @Inject
    NightWorkerMongoRepository nightWorkerMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    @Inject
    private StaffQuestionnaireMongoRepository staffQuestionnaireMongoRepository;

    @Inject
    private StaffRestClient staffRestClient;

    @Inject
    private UnitAgeSettingMongoRepository unitAgeSettingMongoRepository;

    public List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaire(Long unitId, Long staffId){
        return nightWorkerMongoRepository.getNightWorkerQuestionnaireDetails(staffId);
    }

    public NightWorkerGeneralResponseDTO getNightWorkerDetailsOfStaff(Long unitId, Long staffId){

        // TODO set night worker details only if Staff is employed (Unit Position has been created)
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffAndUnitId(staffId, unitId);
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
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffAndUnitId(staffId, unitId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            nightWorker.setNightWorker(nightWorkerDTO.isNightWorker());
            nightWorker.setStartDate(nightWorkerDTO.getStartDate());
            nightWorker.setPersonType(nightWorkerDTO.getPersonType());
            nightWorker.setQuestionnaireFrequencyInMonths(nightWorkerDTO.getQuestionnaireFrequencyInMonths());
            nightWorker.setEligibleNightWorker(nightWorkerDTO.isEligibleNightWorker());
        } else if (!nightWorkerDTO.isNightWorker()){
            return new NightWorkerGeneralResponseDTO(false);
        } else {
            // TODO Set Night worker eligibility check as per the given settings
            nightWorker = new NightWorker(nightWorkerDTO.isNightWorker(), nightWorkerDTO.getStartDate(), nightWorkerDTO.getPersonType(),
                    nightWorkerDTO.getQuestionnaireFrequencyInMonths(), staffId, unitId,nightWorkerDTO.isEligibleNightWorker());
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

        // check if any question is unanswered ( null)
        if(!staffQuestionnaire.isSubmitted() && ! (answerResponseDTO.getQuestionAnswerPair().stream().anyMatch(questionAnswerPair-> !Optional.ofNullable(questionAnswerPair.getAnswer()).isPresent())) ){
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
    public void createNightWorkerQuestionnaireForStaff(Long staffId, Long unitId){

        // Add default questionnaire
        StaffQuestionnaire staffQuestionnaire = addDefaultStaffQuestionnaire();
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffAndUnitId(staffId, unitId);

        // Add in list of questionnaires Ids if already present or set new List with added questionnaire's Id
        if(Optional.ofNullable(nightWorker.getStaffQuestionnairesId()).isPresent()){
            nightWorker.getStaffQuestionnairesId().add(staffQuestionnaire.getId());
        } else {
            nightWorker.setStaffQuestionnairesId(new ArrayList<BigInteger>() {{
                add(staffQuestionnaire.getId());
            }});
        }
        save(nightWorker);
    }

    public void updateNightWorkerEligibilityDetails(Long unitId, Long staffId, boolean eligibleForNightWorker, List<NightWorker> nightWorkers){

        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffAndUnitId(staffId, unitId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            nightWorker.setEligibleNightWorker(eligibleForNightWorker);
        } else {
            nightWorker = new NightWorker(false, null, null,0, staffId, unitId, eligibleForNightWorker);
            StaffQuestionnaire staffQuestionnaire = addDefaultStaffQuestionnaire();
            nightWorker.setStaffQuestionnairesId(new ArrayList<BigInteger>() {{
                add(staffQuestionnaire.getId());
            }});
        }
        nightWorkers.add(nightWorker);
    }

    public void updateNightWorkerEligibilityOfStaffInUnit(Map<Long, List<Long>> staffEligibleForNightWorker, Map<Long, List<Long>> staffNotEligibleForNightWorker){

        List<NightWorker> nightWorkers = new ArrayList<>();
        staffEligibleForNightWorker.forEach((unitId, staffIds) -> {
            staffIds.stream().forEach(staffId -> {
                updateNightWorkerEligibilityDetails(unitId, staffId, true, nightWorkers);
            });
        });

        staffNotEligibleForNightWorker.forEach((unitId, staffIds) -> {
            staffIds.stream().forEach(staffId -> {
                updateNightWorkerEligibilityDetails(unitId, staffId, false, nightWorkers);
            });
        });
        save(nightWorkers);
    }

    public void checkIfStaffAreEligibleForNightWorker(UnitAgeSetting unitAgeSetting, List<StaffDTO> staffList ,
                                                      Map<Long, List<Long>> staffEligibleForNightWorker , Map<Long, List<Long>> staffNotEligibleForNightWorker ){

        List<Long> staffIdsEligibleForNightWorker = new ArrayList<>();
        List<Long> staffIdsNotEligibleForNightWorker = new ArrayList<>();
        staffList.stream().forEach(staffDTO -> {
            Specification<StaffDTO> nightWorkerAgeSpecification = new NightWorkerAgeEligibilitySpecification(unitAgeSetting.getYounger(),
                    unitAgeSetting.getOlder());
            Specification<StaffDTO> nightWorkerPregnancySpecification = new StaffNonPregnancySpecification();
            Specification<StaffDTO> rulesSpecification = nightWorkerAgeSpecification.and(nightWorkerPregnancySpecification);

            if(rulesSpecification.isSatisfied(staffDTO)){
                staffIdsEligibleForNightWorker.add(staffDTO.getId());
            } else {
                staffIdsNotEligibleForNightWorker.add(staffDTO.getId());
            }
        });
        if(!staffIdsEligibleForNightWorker.isEmpty()){
            staffEligibleForNightWorker.put(unitAgeSetting.getUnitId(), staffIdsEligibleForNightWorker);
        }
        if(!staffIdsNotEligibleForNightWorker.isEmpty()){
            staffNotEligibleForNightWorker.put(unitAgeSetting.getUnitId(), staffIdsNotEligibleForNightWorker);
        }

    }

    // Method to be triggered when job will be executed for updating eligibility of Staff for being night worker
    public boolean updateNightWorkerEligibilityOfStaff(){
        List<UnitStaffResponseDTO> unitStaffResponseDTOs = staffRestClient.getUnitWiseStaffList();
        List<Long> listOfUnitIds = new ArrayList<Long>();
        unitStaffResponseDTOs.stream().forEach(unitStaffResponseDTO ->{
            listOfUnitIds.add(unitStaffResponseDTO.getUnitId());
        });
        List<UnitAgeSetting> nightWorkerUnitSettings = unitAgeSettingMongoRepository.findByUnitIds(listOfUnitIds);
        Map<Long, UnitAgeSetting> nightWorkerUnitSettingsMap = new HashMap<>();
        nightWorkerUnitSettings.stream().forEach(nightWorkerUnitSetting -> {
            nightWorkerUnitSettingsMap.put(nightWorkerUnitSetting.getUnitId(), nightWorkerUnitSetting);
        });
        Map<Long, List<Long>> staffEligibleForNightWorker = new HashMap<>();
        Map<Long, List<Long>> staffNotEligibleForNightWorker = new HashMap<>();

        unitStaffResponseDTOs.stream().forEach(unitStaffResponseDTO -> {
            checkIfStaffAreEligibleForNightWorker(nightWorkerUnitSettingsMap.get(unitStaffResponseDTO.getUnitId()), unitStaffResponseDTO.getStaffList(), staffEligibleForNightWorker,
                    staffNotEligibleForNightWorker);
        });
        return true;
    }

}
