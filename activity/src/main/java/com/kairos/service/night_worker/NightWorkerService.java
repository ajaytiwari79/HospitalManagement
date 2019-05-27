package com.kairos.service.night_worker;

import com.kairos.commons.utils.*;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.night_worker.*;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.dto.user.staff.staff.UnitStaffResponseDTO;
import com.kairos.enums.CalculationUnit;
import com.kairos.persistence.model.night_worker.*;
import com.kairos.persistence.model.unit_settings.UnitAgeSetting;
import com.kairos.persistence.repository.night_worker.*;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitAgeSettingMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.night_worker.NightWorkerAgeEligibilitySpecification;
import com.kairos.rule_validator.night_worker.StaffNonPregnancySpecification;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_QUESTIONNAIRE_FREQUENCY;

/**
 * Created by prerna on 8/5/18.
 */
@Service
@Transactional
public class NightWorkerService{

    @Inject
    NightWorkerMongoRepository nightWorkerMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    @Inject
    private StaffQuestionnaireMongoRepository staffQuestionnaireMongoRepository;

    @Inject
    private UserIntegrationService userIntegrationService;

    @Inject
    private UnitAgeSettingMongoRepository unitAgeSettingMongoRepository;
    @Inject private ExpertiseNightWorkerSettingRepository expertiseNightWorkerSettingRepository;
    @Inject private ShiftMongoRepository shiftMongoRepository;

    public List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaire(Long staffId){
        return nightWorkerMongoRepository.getNightWorkerQuestionnaireDetails(staffId);
    }

    public NightWorkerGeneralResponseDTO getNightWorkerDetailsOfStaff(Long unitId, Long staffId){

        // TODO set night worker details only if Staff is employed (Employment has been created)
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffAndUnitId(staffId, unitId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            return ObjectMapperUtils.copyPropertiesByMapper(nightWorker, NightWorkerGeneralResponseDTO.class);
        } else {
            return new NightWorkerGeneralResponseDTO(false);
        }
    }

    public void validateNightWorkerGeneralDetails(NightWorkerGeneralResponseDTO nightWorkerDTO){
        if(nightWorkerDTO.getQuestionnaireFrequencyInMonths() <= 0){
            exceptionService.dataNotFoundByIdException(MESSAGE_QUESTIONNAIRE_FREQUENCY);
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
            nightWorker.setStaffQuestionnairesId(newArrayList(staffQuestionnaire.getId()));
        }
        nightWorkerMongoRepository.save(nightWorker);
        return ObjectMapperUtils.copyPropertiesByMapper(nightWorker, NightWorkerGeneralResponseDTO.class);
    }



    public String prepareNameOfQuestionnaireSet(){
        return AppConstants.QUESTIONNAIE_NAME_PREFIX + " " + DateUtils.getDateString(DateUtils.getDate(), "dd_MMM_yyyy");
    }

    public QuestionnaireAnswerResponseDTO updateNightWorkerQuestionnaire(BigInteger questionnaireId, QuestionnaireAnswerResponseDTO answerResponseDTO){

        StaffQuestionnaire staffQuestionnaire = staffQuestionnaireMongoRepository.findByIdAndDeleted(questionnaireId);

        // check if any question is unanswered ( null)
        if(!staffQuestionnaire.isSubmitted() && ! (answerResponseDTO.getQuestionAnswerPair().stream().anyMatch(questionAnswerPair-> !Optional.ofNullable(questionAnswerPair.getAnswer()).isPresent())) ){
            staffQuestionnaire.setSubmitted(true);
            staffQuestionnaire.setSubmittedOn(DateUtils.getLocalDateFromDate(DateUtils.getDate()));
            answerResponseDTO.setSubmitted(true);
            answerResponseDTO.setSubmittedOn(staffQuestionnaire.getSubmittedOn());
        }
        staffQuestionnaire.setQuestionAnswerPair(ObjectMapperUtils.copyPropertiesOfListByMapper(answerResponseDTO.getQuestionAnswerPair(), QuestionAnswerPair.class));
        staffQuestionnaireMongoRepository.save(staffQuestionnaire);
        return answerResponseDTO;
    }

    public StaffQuestionnaire addDefaultStaffQuestionnaire(){
        List<QuestionAnswerDTO>  questionnaire = nightWorkerMongoRepository.getNightWorkerQuestions();
        StaffQuestionnaire staffQuestionnaire = new StaffQuestionnaire(
                prepareNameOfQuestionnaireSet(),
                ObjectMapperUtils.copyPropertiesOfListByMapper(questionnaire, QuestionAnswerPair.class));
        staffQuestionnaireMongoRepository.save(staffQuestionnaire);
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
            nightWorker.setStaffQuestionnairesId(newArrayList(staffQuestionnaire.getId()));
        }
        nightWorkerMongoRepository.save(nightWorker);
    }

    public void updateNightWorkerEligibilityDetails(Long unitId, Long staffId, boolean eligibleForNightWorker, List<NightWorker> nightWorkers){

        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffAndUnitId(staffId, unitId);
        if(Optional.ofNullable(nightWorker).isPresent()){
            nightWorker.setEligibleNightWorker(eligibleForNightWorker);
        } else {
            nightWorker = new NightWorker(false, null, null,0, staffId, unitId, eligibleForNightWorker);
            StaffQuestionnaire staffQuestionnaire = addDefaultStaffQuestionnaire();
            nightWorker.setStaffQuestionnairesId(newArrayList(staffQuestionnaire.getId()));
        }
        nightWorkers.add(nightWorker);
    }

    public void updateNightWorkerEligibilityOfStaffInUnit(Map<Long, List<Long>> staffEligibleForNightWorker, Map<Long, List<Long>> staffNotEligibleForNightWorker){

        List<NightWorker> nightWorkers = new ArrayList<>();
        staffEligibleForNightWorker.forEach((unitId, staffIds) ->
            staffIds.stream().forEach(staffId -> updateNightWorkerEligibilityDetails(unitId, staffId, true, nightWorkers))
        );

        staffNotEligibleForNightWorker.forEach((unitId, staffIds) ->
            staffIds.stream().forEach(staffId ->updateNightWorkerEligibilityDetails(unitId, staffId, false, nightWorkers)));
        nightWorkerMongoRepository.saveEntities(nightWorkers);
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
        List<UnitStaffResponseDTO> unitStaffResponseDTOs = userIntegrationService.getUnitWiseStaffList();
        List<Long> listOfUnitIds = new ArrayList<>();
        unitStaffResponseDTOs.stream().forEach(unitStaffResponseDTO ->
            listOfUnitIds.add(unitStaffResponseDTO.getUnitId())
        );
        List<UnitAgeSetting> nightWorkerUnitSettings = unitAgeSettingMongoRepository.findByUnitIds(listOfUnitIds);
        Map<Long, UnitAgeSetting> nightWorkerUnitSettingsMap = new HashMap<>();
        nightWorkerUnitSettings.stream().forEach(nightWorkerUnitSetting -> nightWorkerUnitSettingsMap.put(nightWorkerUnitSetting.getUnitId(), nightWorkerUnitSetting));
        Map<Long, List<Long>> staffEligibleForNightWorker = new HashMap<>();
        Map<Long, List<Long>> staffNotEligibleForNightWorker = new HashMap<>();

        unitStaffResponseDTOs.stream().forEach(unitStaffResponseDTO -> checkIfStaffAreEligibleForNightWorker(nightWorkerUnitSettingsMap.get(unitStaffResponseDTO.getUnitId()), unitStaffResponseDTO.getStaffList(), staffEligibleForNightWorker,
                    staffNotEligibleForNightWorker)
        );
        return true;
    }

    public Map<Long,Boolean> updateNightWorkers(Map<Long,Long> employmentAndExpertiseIdMap){
        List<ExpertiseNightWorkerSetting> expertiseNightWorkerSettings = expertiseNightWorkerSettingRepository.findAllByExpertiseIds(employmentAndExpertiseIdMap.values());
        Map<Long,ExpertiseNightWorkerSetting> expertiseNightWorkerSettingMap = expertiseNightWorkerSettings.stream().collect(Collectors.toMap(ExpertiseNightWorkerSetting::getExpertiseId,v->v));
        Map<Long,Boolean> employmentAndNightWorkerMap = new HashMap<>();
        for (Map.Entry<Long, Long> employmentAndExpertiseIdEntry : employmentAndExpertiseIdMap.entrySet()) {
            if(expertiseNightWorkerSettingMap.containsKey(employmentAndExpertiseIdEntry.getValue())) {
                ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingMap.get(employmentAndExpertiseIdEntry.getValue());
                DateTimeInterval dateTimeInterval = getIntervalByNightWorkerSetting(expertiseNightWorkerSetting);
                List<ShiftDTO> shiftDTOS = shiftMongoRepository.findAllShiftBetweenDuration(employmentAndExpertiseIdEntry.getKey(), dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate());
                int minutesOrCount = getNightMinutesOrCount(expertiseNightWorkerSetting, shiftDTOS);
                boolean nightWorker = isNightWorker(expertiseNightWorkerSetting, minutesOrCount, shiftDTOS.size());
                employmentAndNightWorkerMap.put(employmentAndExpertiseIdEntry.getKey(), nightWorker);
            }
        }
        return employmentAndNightWorkerMap;
    }

    private int getNightMinutesOrCount(ExpertiseNightWorkerSetting expertiseNightWorkerSetting, List<ShiftDTO> shiftDTOS) {
        int minutesOrCount = 0;
        for (ShiftDTO shiftDTO : shiftDTOS) {
            DateTimeInterval nightInterval = getNightInterval(shiftDTO.getStartDate(), shiftDTO.getEndDate(), expertiseNightWorkerSetting.getTimeSlot());
            if (nightInterval.overlaps(shiftDTO.getInterval())) {
                int overlapMinutes = (int) nightInterval.overlap(shiftDTO.getInterval()).getMinutes();
                if (overlapMinutes >= expertiseNightWorkerSetting.getMinMinutesToCheckNightShift()) {
                    if (expertiseNightWorkerSetting.getMinShiftsUnitToCheckNightWorker().equals(CalculationUnit.HOURS)) {
                        minutesOrCount += (int) shiftDTO.getInterval().getMinutes();
                    } else {
                        minutesOrCount++;
                    }
                }
            }
        }
        return minutesOrCount;
    }

    private boolean isNightWorker(ExpertiseNightWorkerSetting expertiseNightWorkerSetting, int minutesOrCount,int shiftCount) {
        return isNightHoursValid(expertiseNightWorkerSetting, minutesOrCount, CalculationUnit.HOURS) || isNightHoursValid(expertiseNightWorkerSetting, (minutesOrCount * 100) / shiftCount, CalculationUnit.PERCENTAGE);
    }

    private boolean isNightHoursValid(ExpertiseNightWorkerSetting expertiseNightWorkerSetting, int minutesOrCount, CalculationUnit calculationUnit) {
        return expertiseNightWorkerSetting.getMinShiftsUnitToCheckNightWorker().equals(calculationUnit) && minutesOrCount >= expertiseNightWorkerSetting.getMinShiftsValueToCheckNightWorker();
    }

    public static DateTimeInterval getNightInterval(Date startDate,Date endDate, TimeSlot timeSlot){
        LocalDate startLocalDate = asLocalDate(startDate);
        LocalDate endLocalDate = LocalTime.of(timeSlot.getStartHour(),timeSlot.getStartMinute()).isAfter(LocalTime.of(timeSlot.getEndHour(),timeSlot.getEndMinute())) ? startLocalDate.plusDays(1) : startLocalDate;
        return new DateTimeInterval(asDate(startLocalDate, LocalTime.of(timeSlot.getStartHour(),timeSlot.getStartMinute())),asDate(endLocalDate, LocalTime.of(timeSlot.getEndHour(),timeSlot.getEndMinute())));
    }

    private DateTimeInterval getIntervalByNightWorkerSetting(ExpertiseNightWorkerSetting expertiseNightWorkerSetting){
        DateTimeInterval interval = null;
        if (expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker() != 0 && isNotNull(expertiseNightWorkerSetting.getIntervalUnitToCheckNightWorker())) {
            LocalDate localDate = LocalDate.now();
            switch (expertiseNightWorkerSetting.getIntervalUnitToCheckNightWorker()) {
                case DAYS:
                    interval = new DateTimeInterval(asDate(localDate.minusDays( expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())), getEndOfDayDateFromLocalDate(localDate.plusDays( expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())));
                    break;
                case WEEKS:
                    interval = new DateTimeInterval(asDate(localDate.minusWeeks(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())), getEndOfDayDateFromLocalDate(localDate.plusWeeks( expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())));
                    break;
                case MONTHS:
                    interval = new DateTimeInterval(asDate(localDate.minusMonths(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())), getEndOfDayDateFromLocalDate(localDate.plusMonths( expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())));
                    break;
                case YEAR:
                    interval = new DateTimeInterval(asDate(localDate.minusYears(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())), getEndOfDayDateFromLocalDate(localDate.plusYears( expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())));
                    break;
                default:
                    break;
            }
        }
        return interval;
    }

}
