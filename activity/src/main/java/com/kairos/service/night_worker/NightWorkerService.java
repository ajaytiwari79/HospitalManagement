package com.kairos.service.night_worker;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.night_worker.NightWorkerGeneralResponseDTO;
import com.kairos.dto.activity.night_worker.QuestionAnswerDTO;
import com.kairos.dto.activity.night_worker.QuestionnaireAnswerResponseDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user.staff.staff.UnitStaffResponseDTO;
import com.kairos.enums.CalculationUnit;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.model.night_worker.NightWorker;
import com.kairos.persistence.model.night_worker.QuestionAnswerPair;
import com.kairos.persistence.model.night_worker.StaffQuestionnaire;
import com.kairos.persistence.model.unit_settings.UnitAgeSetting;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.DaysOffAfterASeriesWTATemplate;
import com.kairos.persistence.repository.night_worker.ExpertiseNightWorkerSettingRepository;
import com.kairos.persistence.repository.night_worker.NightWorkerMongoRepository;
import com.kairos.persistence.repository.night_worker.StaffQuestionnaireMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitAgeSettingMongoRepository;
import com.kairos.persistence.repository.wta.WorkingTimeAgreementMongoRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.night_worker.NightWorkerAgeEligibilitySpecification;
import com.kairos.rule_validator.night_worker.StaffNonPregnancySpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftFilterService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_QUESTIONNAIRE_FREQUENCY;

/**
 * Created by prerna on 8/5/18.
 */
@Service
@Transactional
public class NightWorkerService {

    @Inject
    private NightWorkerMongoRepository nightWorkerMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private StaffQuestionnaireMongoRepository staffQuestionnaireMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private UnitAgeSettingMongoRepository unitAgeSettingMongoRepository;
    @Inject
    private ExpertiseNightWorkerSettingRepository expertiseNightWorkerSettingRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private WorkingTimeAgreementMongoRepository workingTimeAgreementMongoRepository;
    @Inject
    private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private SchedulerServiceRestClient schedulerRestClient;
    @Inject
    private ShiftFilterService shiftFilterService;

    public String prepareNameOfQuestionnaireSet() {
        return AppConstants.QUESTIONNAIE_NAME_PREFIX + " " + DateUtils.getDateString(DateUtils.getDate(), "dd_MMM_yyyy");
    }

    public List<QuestionnaireAnswerResponseDTO> getNightWorkerQuestionnaire(Long staffId) {
        return nightWorkerMongoRepository.getNightWorkerQuestionnaireDetails(staffId);
    }

    public NightWorkerGeneralResponseDTO getNightWorkerDetailsOfStaff(Long unitId, Long staffId) {

        // TODO set night worker details only if Staff is employed (Employment has been created)
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if (Optional.ofNullable(nightWorker).isPresent()) {
            return ObjectMapperUtils.copyPropertiesByMapper(nightWorker, NightWorkerGeneralResponseDTO.class);
        } else {
            return new NightWorkerGeneralResponseDTO(false);
        }
    }

    public void validateNightWorkerGeneralDetails(NightWorkerGeneralResponseDTO nightWorkerDTO) {
        if (nightWorkerDTO.getQuestionnaireFrequencyInMonths() <= 0) {
            exceptionService.dataNotFoundByIdException(MESSAGE_QUESTIONNAIRE_FREQUENCY);
        }
    }

    public NightWorkerGeneralResponseDTO updateNightWorkerGeneralDetails(Long unitId, Long staffId, NightWorkerGeneralResponseDTO nightWorkerDTO) {

        validateNightWorkerGeneralDetails(nightWorkerDTO);
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if (Optional.ofNullable(nightWorker).isPresent()) {
            nightWorker.setNightWorker(nightWorkerDTO.isNightWorker());
            nightWorker.setStartDate(nightWorkerDTO.getStartDate());
            nightWorker.setPersonType(nightWorkerDTO.getPersonType());
            nightWorker.setQuestionnaireFrequencyInMonths(nightWorkerDTO.getQuestionnaireFrequencyInMonths());
            nightWorker.setEligibleNightWorker(nightWorkerDTO.isEligibleNightWorker());
        } else if (!nightWorkerDTO.isNightWorker()) {
            return new NightWorkerGeneralResponseDTO(false);
        } else {
            // TODO Set Night worker eligibility check as per the given settings
            nightWorker = new NightWorker(nightWorkerDTO.isNightWorker(), nightWorkerDTO.getStartDate(), nightWorkerDTO.getPersonType(),
                    nightWorkerDTO.getQuestionnaireFrequencyInMonths(), staffId, unitId, nightWorkerDTO.isEligibleNightWorker());
            StaffQuestionnaire staffQuestionnaire = addDefaultStaffQuestionnaire();
            nightWorker.setStaffQuestionnairesId(new ArrayList<BigInteger>() {{
                add(staffQuestionnaire.getId());
            }});
        }
        nightWorkerMongoRepository.save(nightWorker);
        return ObjectMapperUtils.copyPropertiesByMapper(nightWorker, NightWorkerGeneralResponseDTO.class);
    }

    public QuestionnaireAnswerResponseDTO updateNightWorkerQuestionnaire(BigInteger questionnaireId, QuestionnaireAnswerResponseDTO answerResponseDTO) {

        StaffQuestionnaire staffQuestionnaire = staffQuestionnaireMongoRepository.findByIdAndDeleted(questionnaireId);

        // check if any question is unanswered ( null)
        if (!staffQuestionnaire.isSubmitted() && !(answerResponseDTO.getQuestionAnswerPair().stream().anyMatch(questionAnswerPair -> !Optional.ofNullable(questionAnswerPair.getAnswer()).isPresent()))) {
            staffQuestionnaire.setSubmitted(true);
            staffQuestionnaire.setSubmittedOn(DateUtils.getLocalDateFromDate(DateUtils.getDate()));
            answerResponseDTO.setSubmitted(true);
            answerResponseDTO.setSubmittedOn(staffQuestionnaire.getSubmittedOn());
        }
        staffQuestionnaire.setQuestionAnswerPair(ObjectMapperUtils.copyPropertiesOfListByMapper(answerResponseDTO.getQuestionAnswerPair(), QuestionAnswerPair.class));
        staffQuestionnaireMongoRepository.save(staffQuestionnaire);
        return answerResponseDTO;
    }

    public StaffQuestionnaire addDefaultStaffQuestionnaire() {
        List<QuestionAnswerDTO> questionnaire = nightWorkerMongoRepository.getNightWorkerQuestions();
        StaffQuestionnaire staffQuestionnaire = new StaffQuestionnaire(
                prepareNameOfQuestionnaireSet(),
                ObjectMapperUtils.copyPropertiesOfListByMapper(questionnaire, QuestionAnswerPair.class));
        staffQuestionnaireMongoRepository.save(staffQuestionnaire);
        return staffQuestionnaire;
    }

    // Function will called for scheduled job
    public void createNightWorkerQuestionnaireForStaff(Long staffId, Long unitId) {

        // Add default questionnaire
        StaffQuestionnaire staffQuestionnaire = addDefaultStaffQuestionnaire();
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);

        // Add in list of questionnaires Ids if already present or set new List with added questionnaire's Id
        if (Optional.ofNullable(nightWorker.getStaffQuestionnairesId()).isPresent()) {
            nightWorker.getStaffQuestionnairesId().add(staffQuestionnaire.getId());
        } else {
            nightWorker.setStaffQuestionnairesId(new ArrayList<BigInteger>() {{
                add(staffQuestionnaire.getId());
            }});
        }
        nightWorkerMongoRepository.save(nightWorker);
    }

    public void updateNightWorkerEligibilityDetails(Long unitId, Long staffId, boolean eligibleForNightWorker, List<NightWorker> nightWorkers) {

        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(staffId);
        if (Optional.ofNullable(nightWorker).isPresent()) {
            nightWorker.setEligibleNightWorker(eligibleForNightWorker);
        } else {
            nightWorker = new NightWorker(false, null, null, 0, staffId, unitId, eligibleForNightWorker);
            StaffQuestionnaire staffQuestionnaire = addDefaultStaffQuestionnaire();
            nightWorker.setStaffQuestionnairesId(new ArrayList<BigInteger>() {{
                add(staffQuestionnaire.getId());
            }});
        }
        nightWorkers.add(nightWorker);
    }

    public void updateNightWorkerEligibilityOfStaffInUnit(Map<Long, List<Long>> staffEligibleForNightWorker, Map<Long, List<Long>> staffNotEligibleForNightWorker) {

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
        nightWorkerMongoRepository.saveEntities(nightWorkers);
    }

    public void checkIfStaffAreEligibleForNightWorker(UnitAgeSetting unitAgeSetting, List<StaffDTO> staffList,
                                                      Map<Long, List<Long>> staffEligibleForNightWorker, Map<Long, List<Long>> staffNotEligibleForNightWorker) {

        List<Long> staffIdsEligibleForNightWorker = new ArrayList<>();
        List<Long> staffIdsNotEligibleForNightWorker = new ArrayList<>();
        staffList.stream().forEach(staffDTO -> {
            Specification<StaffDTO> nightWorkerAgeSpecification = new NightWorkerAgeEligibilitySpecification(unitAgeSetting.getYounger(),
                    unitAgeSetting.getOlder());
            Specification<StaffDTO> nightWorkerPregnancySpecification = new StaffNonPregnancySpecification();
            Specification<StaffDTO> rulesSpecification = nightWorkerAgeSpecification.and(nightWorkerPregnancySpecification);

            if (rulesSpecification.isSatisfied(staffDTO)) {
                staffIdsEligibleForNightWorker.add(staffDTO.getId());
            } else {
                staffIdsNotEligibleForNightWorker.add(staffDTO.getId());
            }
        });
        if (!staffIdsEligibleForNightWorker.isEmpty()) {
            staffEligibleForNightWorker.put(unitAgeSetting.getUnitId(), staffIdsEligibleForNightWorker);
        }
        if (!staffIdsNotEligibleForNightWorker.isEmpty()) {
            staffNotEligibleForNightWorker.put(unitAgeSetting.getUnitId(), staffIdsNotEligibleForNightWorker);
        }

    }

    // Method to be triggered when job will be executed for updating eligibility of Staff for being night worker
    public boolean updateNightWorkerEligibilityOfStaff() {
        List<UnitStaffResponseDTO> unitStaffResponseDTOs = userIntegrationService.getUnitWiseStaffList();
        List<Long> listOfUnitIds = new ArrayList<Long>();
        unitStaffResponseDTOs.stream().forEach(unitStaffResponseDTO -> {
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

    public void updateNightWorkers(List<Map> staffAndEmploymentIdMap) {
        Map[] staffAndEmploymentAndExpertiseIdArray = getEmploymentAndExpertiseIdMap(staffAndEmploymentIdMap);
        Map<Long, Long> employmentAndExpertiseIdMap = staffAndEmploymentAndExpertiseIdArray[0];
        Map<Long, Long> employmentIdAndStaffIdMap = staffAndEmploymentAndExpertiseIdArray[1];
        Map<Long, Long> employmentIdAndUnitIdMap = staffAndEmploymentAndExpertiseIdArray[2];
        Map[] nightWorkerDetailsMap = getNightWorkerDetails(employmentAndExpertiseIdMap, employmentIdAndStaffIdMap, employmentIdAndUnitIdMap);
        Map<Long, Boolean> staffIdAndnightWorkerDetailsMap = nightWorkerDetailsMap[0];
        Map<Long, Boolean> employementIdAndNightWorkerMap = nightWorkerDetailsMap[1];
        updateWTARuleTemplateForNightWorker(employementIdAndNightWorkerMap);
        List<NightWorker> nightWorkers = nightWorkerMongoRepository.findByStaffIds(employmentIdAndStaffIdMap.values());
        Map<Long, NightWorker> nightWorkerMap = nightWorkers.stream().collect(Collectors.toMap(NightWorker::getStaffId, v -> v));
        List<NightWorker> updateNightWorkers = new ArrayList<>();
        for (Map.Entry<Long, Boolean> staffIdAndNightWorkerEntry : staffIdAndnightWorkerDetailsMap.entrySet()) {
            NightWorker nightWorker;
            nightWorker = nightWorkerMap.getOrDefault(staffIdAndNightWorkerEntry.getKey(), new NightWorker(false, null, null, 0, staffIdAndNightWorkerEntry.getKey(), null, false));
            nightWorker.setNightWorker(staffIdAndNightWorkerEntry.getValue());
            updateNightWorkers.add(nightWorker);
        }
        if (isCollectionNotEmpty(updateNightWorkers)) {
            nightWorkerMongoRepository.saveEntities(updateNightWorkers);
        }
    }

    private Map[] getEmploymentAndExpertiseIdMap(List<Map> staffAndEmploymentIdMap) {
        Map<Long, Long> employmentAndExpertiseIdMap = new HashMap<>();
        Map<Long, Long> employmentIdAndStaffIdMap = new HashMap<>();
        Map<Long, Long> employmentIdAndUnitIdMap = new HashMap<>();
        for (Map<Long, Map<String, Object>> map : staffAndEmploymentIdMap) {
            List<Map> employmentDetails = (List<Map>) map.get("employmentDetails");
            for (Map employmentDetail : employmentDetails) {
                Long employmentId = ((Integer) employmentDetail.get("id")).longValue();
                employmentAndExpertiseIdMap.put(employmentId, ((Integer) employmentDetail.get("expId")).longValue());
                employmentIdAndStaffIdMap.put(employmentId, ((Integer) ((Object) map.get("staffId"))).longValue());
                employmentIdAndUnitIdMap.put(employmentId, ((Integer)  employmentDetail.get("unitId")).longValue());
            }

        }
        return new Map[]{employmentAndExpertiseIdMap, employmentIdAndStaffIdMap, employmentIdAndUnitIdMap};
    }

    public Map[] getNightWorkerDetails(Map<Long, Long> employmentAndExpertiseIdMap, Map<Long, Long> employmentIdAndStaffIdMap, Map<Long, Long> employmentIdAndUnitIdMap) {
        Map<Long, ExpertiseNightWorkerSetting> expertiseNightWorkerSettingMap = getMapOfExpetiseNightWorkerSetting(employmentAndExpertiseIdMap, employmentIdAndUnitIdMap);
        Map<Long, Boolean> staffIdAndNightWorkerMap = new HashMap<>();
        Map<Long, Boolean> employementIdAndNightWorkerMap = new HashMap<>();
        for (Map.Entry<Long, Long> employmentAndExpertiseIdEntry : employmentAndExpertiseIdMap.entrySet()) {
            boolean nightWorker = false;
            if (expertiseNightWorkerSettingMap.containsKey(employmentAndExpertiseIdEntry.getKey())) {
                ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingMap.get(employmentAndExpertiseIdEntry.getKey());
                if (isNotNull(expertiseNightWorkerSetting.getIntervalUnitToCheckNightWorker()) && isNotNull(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())) {
                    DateTimeInterval dateTimeInterval = getIntervalByNightWorkerSetting(expertiseNightWorkerSetting);
                    List<ShiftDTO> shiftDTOS = shiftMongoRepository.findAllShiftBetweenDuration(employmentAndExpertiseIdEntry.getKey(), dateTimeInterval.getStartDate(), dateTimeInterval.getEndDate());
                    int minutesOrCount = getNightMinutesOrCount(expertiseNightWorkerSetting, shiftDTOS);
                    if (isCollectionNotEmpty(shiftDTOS)) {
                        nightWorker = isNightWorker(expertiseNightWorkerSetting, minutesOrCount, shiftDTOS.size());
                    }
                }
            }
            Long staffId = employmentIdAndStaffIdMap.get(employmentAndExpertiseIdEntry.getKey());
            if (!staffIdAndNightWorkerMap.containsKey(staffId) || !staffIdAndNightWorkerMap.get(staffId)) {
                staffIdAndNightWorkerMap.put(staffId, nightWorker);
                employementIdAndNightWorkerMap.put(employmentAndExpertiseIdEntry.getKey(), nightWorker);
            }
        }
        return new Map[]{staffIdAndNightWorkerMap, employementIdAndNightWorkerMap};
    }

    private Map<Long, ExpertiseNightWorkerSetting> getMapOfExpetiseNightWorkerSetting(Map<Long, Long> employmentAndExpertiseIdMap, Map<Long, Long> employmentIdAndUnitIdMap) {
        List<ExpertiseNightWorkerSetting> expertiseNightWorkerSettingsOfUnits = expertiseNightWorkerSettingRepository.findAllByExpertiseIdsOfUnit(employmentAndExpertiseIdMap.values());
        List<ExpertiseNightWorkerSetting> expertiseNightWorkerSettingsOfCountry = expertiseNightWorkerSettingRepository.findAllByExpertiseIdsOfCountry(employmentAndExpertiseIdMap.values());
        Map<Long, ExpertiseNightWorkerSetting> countryExpertiseAndExpertiseNightWorkerSettingMap = expertiseNightWorkerSettingsOfCountry.stream().collect(Collectors.toMap(k -> k.getExpertiseId(), v -> v));
        Map<String, ExpertiseNightWorkerSetting> unitAndExpertiseNightWorkerSettingMap = expertiseNightWorkerSettingsOfUnits.stream().collect(Collectors.toMap(k -> k.getUnitId()+"_"+k.getExpertiseId(), v -> v));
        Map<Long, ExpertiseNightWorkerSetting> employmentAndExpertiseNightWorkerSettingMap = new HashMap<>();
        for (Long employmentId : employmentIdAndUnitIdMap.keySet()) {
            if (unitAndExpertiseNightWorkerSettingMap.containsKey(employmentIdAndUnitIdMap.get(employmentId)+"_"+employmentAndExpertiseIdMap.get(employmentId))) {
                employmentAndExpertiseNightWorkerSettingMap.put(employmentId,unitAndExpertiseNightWorkerSettingMap.get(employmentIdAndUnitIdMap.get(employmentId)+"_"+employmentAndExpertiseIdMap.get(employmentId)));
            }else {
                employmentAndExpertiseNightWorkerSettingMap.put(employmentId,countryExpertiseAndExpertiseNightWorkerSettingMap.get(employmentAndExpertiseIdMap.get(employmentId)));
            }
        }
        return employmentAndExpertiseNightWorkerSettingMap;
    }

    private void updateWTARuleTemplateForNightWorker(Map<Long, Boolean> employmentAndNightWorkerMap) {
        List<WTAQueryResultDTO> workingTimeAgreements = workingTimeAgreementMongoRepository.getAllWTAByEmploymentIds(employmentAndNightWorkerMap.keySet());
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        for (WTAQueryResultDTO workingTimeAgreement : workingTimeAgreements) {
            for (WTABaseRuleTemplate ruleTemplate : workingTimeAgreement.getRuleTemplates()) {
                if (ruleTemplate instanceof DaysOffAfterASeriesWTATemplate) {
                    ruleTemplate.setDisabled(!employmentAndNightWorkerMap.get(workingTimeAgreement.getEmploymentId()).booleanValue());
                    wtaBaseRuleTemplates.add(ruleTemplate);
                }
            }
        }
        if (isCollectionNotEmpty(wtaBaseRuleTemplates)) {
            wtaBaseRuleTemplateMongoRepository.saveEntities(wtaBaseRuleTemplates);
        }
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

    private boolean isNightWorker(ExpertiseNightWorkerSetting expertiseNightWorkerSetting, int minutesOrCount, int shiftCount) {
        return isNightHoursValid(expertiseNightWorkerSetting, minutesOrCount, CalculationUnit.HOURS) || isNightHoursValid(expertiseNightWorkerSetting, (minutesOrCount * 100) / shiftCount, CalculationUnit.PERCENTAGE);
    }

    private boolean isNightHoursValid(ExpertiseNightWorkerSetting expertiseNightWorkerSetting, int minutesOrCount, CalculationUnit calculationUnit) {
        return expertiseNightWorkerSetting.getMinShiftsUnitToCheckNightWorker().equals(calculationUnit) && minutesOrCount >= expertiseNightWorkerSetting.getMinShiftsValueToCheckNightWorker();
    }

    public static DateTimeInterval getNightInterval(Date startDate, Date endDate, TimeSlot timeSlot) {
        LocalDate startLocalDate = asLocalDate(startDate);
        LocalDate endLocalDate = LocalTime.of(timeSlot.getStartHour(), timeSlot.getStartMinute()).isAfter(LocalTime.of(timeSlot.getEndHour(), timeSlot.getEndMinute())) ? startLocalDate.plusDays(1) : startLocalDate;
        return new DateTimeInterval(asDate(startLocalDate, LocalTime.of(timeSlot.getStartHour(), timeSlot.getStartMinute())), asDate(endLocalDate, LocalTime.of(timeSlot.getEndHour(), timeSlot.getEndMinute())));
    }

    private DateTimeInterval getIntervalByNightWorkerSetting(ExpertiseNightWorkerSetting expertiseNightWorkerSetting) {
        DateTimeInterval interval = null;
        LocalDate localDate = LocalDate.now();
        switch (expertiseNightWorkerSetting.getIntervalUnitToCheckNightWorker()) {
            case DAYS:
                interval = new DateTimeInterval(asDate(localDate.minusDays(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())), getEndOfDayDateFromLocalDate(localDate.plusDays(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())));
                break;
            case WEEKS:
                interval = new DateTimeInterval(asDate(localDate.minusWeeks(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())), getEndOfDayDateFromLocalDate(localDate.plusWeeks(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())));
                break;
            case MONTHS:
                interval = new DateTimeInterval(asDate(localDate.minusMonths(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())), getEndOfDayDateFromLocalDate(localDate.plusMonths(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())));
                break;
            case YEAR:
                interval = new DateTimeInterval(asDate(localDate.minusYears(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())), getEndOfDayDateFromLocalDate(localDate.plusYears(expertiseNightWorkerSetting.getIntervalValueToCheckNightWorker())));
                break;
            default:
                break;
        }
        return interval;
    }

    public void registerJobForNightWorker() {
        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(newArrayList(DayOfWeek.values()), LocalTime.of(0, 1), JobType.SYSTEM, JobSubType.NIGHT_WORKER, ZoneId.systemDefault().toString());
        List<SchedulerPanelDTO> schedulerPanelDTOS = schedulerRestClient.publishRequest(newArrayList(schedulerPanelDTO), null, false, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
        });
    }

    public Map<Long, Boolean> getFilteredStaffNightWorkerDetails(StaffFilterDTO staffFilterDTO, LocalDate startDate, LocalDate endDate) {
        List<Long> staffIds = staffFilterDTO.getStaffIds();
        if (staffFilterDTO.isValidFilterForShift()) {
            List<ShiftDTO> shiftDTOS = shiftMongoRepository.findAllByStaffIdsAndDeleteFalse(staffFilterDTO.getStaffIds(), startDate, endDate);
            shiftDTOS = shiftFilterService.getShiftsByFilters(shiftDTOS, staffFilterDTO);
            staffIds = shiftDTOS.stream().map(shiftDTO -> shiftDTO.getStaffId()).collect(Collectors.toList());
        }
        List<NightWorker> nightWorker = nightWorkerMongoRepository.findByStaffIds(staffIds);
        Map<Long, Boolean> staffIdAndNightWorkerMap = nightWorker.stream().collect(Collectors.toMap(NightWorker::getStaffId, NightWorker::isNightWorker));
        for (Long staffId : staffIds) {
            if (!staffIdAndNightWorkerMap.containsKey(staffId)) {
                staffIdAndNightWorkerMap.put(staffId, false);
            }
        }
        return staffIdAndNightWorkerMap;
    }

}
