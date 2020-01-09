package com.kairos.service.wta;


import com.kairos.commons.custom_exception.DataNotFoundException;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.model.night_worker.NightWorker;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.DurationBetweenShiftsWTATemplate;
import com.kairos.persistence.model.wta.templates.template_types.ShiftLengthWTATemplate;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.night_worker.ExpertiseNightWorkerSettingRepository;
import com.kairos.persistence.repository.night_worker.NightWorkerMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.persistence.repository.wta.rule_template.WTABaseRuleTemplateMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.shift.ShiftValidatorService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_WTA_NOTFOUND;
import static com.kairos.enums.shift.ShiftEscalationReason.WORK_TIME_AGREEMENT;
import static com.kairos.utils.CPRUtil.getAgeByCPRNumberAndStartDate;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getIntervalByRuleTemplates;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getValueByPhase;
import static java.util.Comparator.comparing;

@Service
public class WTARuleTemplateCalculationService {


    @Inject
    private WorkTimeAgreementService workTimeAgreementService;
    @Inject
    private PhaseService phaseService;
    @Inject
    private ShiftViolatedRulesMongoRepository shiftViolatedRulesMongoRepository;
    @Inject private WTABaseRuleTemplateMongoRepository wtaBaseRuleTemplateMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private ShiftValidatorService shiftValidatorService;
    @Inject private ExceptionService exceptionService;
    @Inject private ShiftService shiftService;
    @Inject private PlanningPeriodService planningPeriodService;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private StaffWTACounterRepository staffWTACounterRepository;
    @Inject
    private ExpertiseNightWorkerSettingRepository expertiseNightWorkerSettingRepository;
    @Inject private
    TimeBankService timeBankService;
    @Inject
    private NightWorkerMongoRepository nightWorkerMongoRepository;

    public <T extends ShiftDTO> List<T> updateRestingTimeInShifts(List<T> shifts) {
        if (isCollectionNotEmpty(shifts)) {
            if (!(shifts instanceof ArrayList)) {
                shifts = new ArrayList<>(shifts);
            }
            Set<BigInteger> activityIds = shifts.stream().flatMap(shiftDTO -> shiftDTO.getActivities().stream()).map(shiftActivityDTO -> shiftActivityDTO.getActivityId()).collect(Collectors.toSet());
            List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
            Map<BigInteger,BigInteger> activityIdAndTimetypeIdMap = activities.stream().collect(Collectors.toMap(k->k.getId(),v->v.getBalanceSettingsActivityTab().getTimeTypeId()));
            shifts.sort(comparing(ShiftDTO::getStartDate));
            Date startDate = getStartOfDay(shifts.get(0).getStartDate());
            Date endDate = getStartOfDay(plusDays(shifts.get(shifts.size() - 1).getEndDate(), 1));
            List<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toList());
            Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(shiftIds,false).stream().collect(Collectors.toMap(k -> k.getShiftId(), v -> v));
            List<WTAQueryResultDTO> workingTimeAgreements = workTimeAgreementService.getWTAByEmploymentIdAndDatesWithRuleTemplateType(shifts.get(0).getEmploymentId(), startDate, endDate, WTATemplateType.DURATION_BETWEEN_SHIFTS);
            Map<DateTimeInterval, List<DurationBetweenShiftsWTATemplate>> intervalWTARuletemplateMap = getIntervalWTARuletemplateMap(workingTimeAgreements, asLocalDate(endDate).plusDays(1));
            Set<LocalDateTime> dateTimes = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getActivities().get(0).getStartDate())).collect(Collectors.toSet());
            Map<Date, Phase> phaseMapByDate = phaseService.getPhasesByDates(shifts.get(0).getUnitId(), dateTimes);
            Set<BigInteger> escalationFreeShiftIds= shiftValidatorService.getEscalationFreeShifts(shifts.stream().map(ShiftDTO::getId).collect(Collectors.toList()));
            for (ShiftDTO shift : shifts) {
                int restingMinutes = getRestingMinutes(activityIdAndTimetypeIdMap, intervalWTARuletemplateMap, phaseMapByDate, shift);
                shift.setRestingMinutes(restingMinutes);
                shift.setEscalationReasons(shiftViolatedRulesMap.containsKey(shift.getId()) ? shiftViolatedRulesMap.get(shift.getId()).getEscalationReasons():newHashSet());
                shift.setEscalationResolved(escalationFreeShiftIds.contains(shift.getId()));
            }
        }
        return shifts;
    }

    private int getRestingMinutes( Map<BigInteger, BigInteger> activityIdAndTimetypeIdMap, Map<DateTimeInterval, List<DurationBetweenShiftsWTATemplate>> intervalWTARuletemplateMap, Map<Date, Phase> phaseMapByDate, ShiftDTO shift) {
        int restingMinutes = 0;
        Map.Entry<DateTimeInterval, List<DurationBetweenShiftsWTATemplate>> dateTimeIntervalListEntry = intervalWTARuletemplateMap.entrySet().stream().filter(dateTimeIntervalList -> dateTimeIntervalList.getKey().contains(shift.getStartDate()) || dateTimeIntervalList.getKey().getEndLocalDate().equals(asLocalDate(shift.getStartDate()))).findAny().orElse(null);
        if (isNotNull(dateTimeIntervalListEntry)) {
            List<DurationBetweenShiftsWTATemplate> durationBetweenShiftsWTATemplates = dateTimeIntervalListEntry.getValue();
            for (DurationBetweenShiftsWTATemplate durationBetweenShiftsWTATemplate : durationBetweenShiftsWTATemplates) {
                boolean anyActivityValid = shift.getActivities().stream().filter(shiftActivityDTO -> durationBetweenShiftsWTATemplate.getTimeTypeIds().contains(activityIdAndTimetypeIdMap.get(shiftActivityDTO.getActivityId()))).findAny().isPresent();
                if(anyActivityValid && phaseMapByDate.containsKey(shift.getStartDate())) {
                    Integer currentRuletemplateRestingMinutes = getValueByPhase( durationBetweenShiftsWTATemplate.getPhaseTemplateValues(), phaseMapByDate.get(shift.getStartDate()).getId());
                    if(isNotNull(currentRuletemplateRestingMinutes) && restingMinutes < currentRuletemplateRestingMinutes) {
                        restingMinutes = currentRuletemplateRestingMinutes;
                    }
                }
            }
        }
        return restingMinutes;
    }

    private Map<DateTimeInterval, List<DurationBetweenShiftsWTATemplate>> getIntervalWTARuletemplateMap(List<WTAQueryResultDTO> workingTimeAgreements, LocalDate endDate) {
        Map<DateTimeInterval, List<DurationBetweenShiftsWTATemplate>> dateTimeIntervalListMap = new HashMap<>(workingTimeAgreements.size());
        for (WTAQueryResultDTO workingTimeAgreement : workingTimeAgreements) {
            DateTimeInterval dateTimeInterval = new DateTimeInterval(workingTimeAgreement.getStartDate(), isNotNull(workingTimeAgreement.getEndDate()) ? workingTimeAgreement.getEndDate() : endDate);
            dateTimeIntervalListMap.put(dateTimeInterval, workingTimeAgreement.getRuleTemplates().stream().map(wtaBaseRuleTemplate -> (DurationBetweenShiftsWTATemplate) wtaBaseRuleTemplate).collect(Collectors.toList()));
        }
        return dateTimeIntervalListMap;
    }

    public void updateWTACounter(Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO){
        WTAQueryResultDTO wtaQueryResultDTO = workTimeAgreementService.getWtaQueryResultDTOByDateAndEmploymentId(shift.getEmploymentId(),shift.getStartDate());
        Map<BigInteger, ActivityWrapper> activityWrapperMap = shiftService.getActivityWrapperMap(isNotNull(shift) ? newArrayList(shift) : newArrayList(), null);
        DateTimeInterval planningPeriodInterval = planningPeriodService.getPlanningPeriodIntervalByUnitId(shift.getUnitId());
        ShiftWithActivityDTO shiftWithActivityDTO = shiftService.buildShiftWithActivityDTOAndUpdateShiftDTOWithActivityName(ObjectMapperUtils.copyPropertiesByMapper(shift,ShiftDTO.class), activityWrapperMap);
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shiftWithActivityDTO, wtaQueryResultDTO.getRuleTemplates(), activityWrapperMap, planningPeriodInterval.getEndLocalDate());
        List<PlanningPeriodDTO> planningPeriodDTOS = planningPeriodService.findAllPlanningPeriodBetweenDatesAndUnitId(shift.getUnitId(),intervalByRuleTemplates.getStartDate(),intervalByRuleTemplates.getEndDate());
        List<WTAQueryResultDTO> wtaQueryResultDTOS = workTimeAgreementService.getWTAByEmploymentIdAndDates(shift.getEmploymentId(),intervalByRuleTemplates.getStartDate(),intervalByRuleTemplates.getEndDate());
        List<ShiftWithActivityDTO> shifts = new ArrayList<>(shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentIdNotEqualShiftIds(shift.getEmploymentId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()),newArrayList(shiftWithActivityDTO.getId())));
        shifts.add(shiftWithActivityDTO);
        List<StaffWTACounter> staffWTACounters = staffWTACounterRepository.getStaffWTACounterBetweenDate(shift.getEmploymentId(),planningPeriodDTOS.get(0).getStartDate(),planningPeriodDTOS.get(planningPeriodDTOS.size()-1).getEndDate(), UserContext.getUserDetails().isStaff());
        intervalByRuleTemplates = getIntervalByShifts(wtaQueryResultDTO, activityWrapperMap, planningPeriodInterval, intervalByRuleTemplates, shifts);
        List<BigInteger> shiftIds = shifts.stream().map(shiftWithActivityDTO1 -> shiftWithActivityDTO1.getId()).collect(Collectors.toList());
        List<ShiftWithActivityDTO> existingShifts = new ArrayList<>(shiftMongoRepository.findAllShiftsBetweenDurationByEmploymentIdNotEqualShiftIds(shift.getEmploymentId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()),shiftIds));
        PlanningPeriod lastPlanningPeriod = planningPeriodService.getLastPlanningPeriod(staffAdditionalInfoDTO.getUnitId());
        existingShifts.addAll(shifts);
        shiftValidatorService.updateFullDayAndFullWeekActivityShift(existingShifts);
        Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(DayTypeDTO::getId, v -> v));
        Map<String, TimeSlotWrapper> timeSlotWrapperMap = staffAdditionalInfoDTO.getTimeSlotSets().stream().collect(Collectors.toMap(TimeSlotWrapper::getName, v -> v));
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = getExpertiseNightWorkerSetting(shift, staffAdditionalInfoDTO);
        NightWorker nightWorker = nightWorkerMongoRepository.findByStaffId(shift.getStaffId());
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = getShiftIdShiftViolatedRulesMap(shiftIds);
        Map<Date, Phase> phaseMapByDate = getDateWisePhaseMap(shift, shifts);
        updateWTACounter(shiftWithActivityDTO, staffAdditionalInfoDTO, activityWrapperMap, planningPeriodDTOS, wtaQueryResultDTOS, shifts, staffWTACounters, existingShifts, lastPlanningPeriod, dayTypeDTOMap, timeSlotWrapperMap, expertiseNightWorkerSetting, nightWorker, shiftViolatedRulesMap, phaseMapByDate);
        staffWTACounterRepository.saveEntities(staffWTACounters);
        shiftViolatedRulesMongoRepository.saveEntities(shiftViolatedRulesMap.values());
    }

    private Map<Date, Phase> getDateWisePhaseMap(Shift shift, List<ShiftWithActivityDTO> shifts) {
        Set<LocalDateTime> dates = shifts.stream().map(s -> DateUtils.asLocalDateTime(s.getStartDate())).collect(Collectors.toSet());
        return phaseService.getPhasesByDates(shift.getUnitId(), dates);
    }

    private Map<BigInteger, ShiftViolatedRules> getShiftIdShiftViolatedRulesMap(List<BigInteger> shiftIds) {
        List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIdsAndWorkTimeAgreement(shiftIds,false);
        return shiftViolatedRules.stream().collect(Collectors.toMap(ShiftViolatedRules::getShiftId, v -> v));
    }

    private void updateWTACounter(ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, List<PlanningPeriodDTO> planningPeriodDTOS, List<WTAQueryResultDTO> wtaQueryResultDTOS, List<ShiftWithActivityDTO> shifts, List<StaffWTACounter> staffWTACounters, List<ShiftWithActivityDTO> existingShifts, PlanningPeriod lastPlanningPeriod, Map<Long, DayTypeDTO> dayTypeDTOMap, Map<String, TimeSlotWrapper> timeSlotWrapperMap, ExpertiseNightWorkerSetting expertiseNightWorkerSetting, NightWorker nightWorker, Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap, Map<Date, Phase> phaseMapByDate) {
        for (ShiftWithActivityDTO updateShiftWithActivityDTO : shifts) {
            Phase phase = phaseMapByDate.get(updateShiftWithActivityDTO.getStartDate());
            LocalDate shiftStartDate = asLocalDate(updateShiftWithActivityDTO.getStartDate());
            if(!shiftStartDate.isBefore(LocalDate.now()) && shiftViolatedRulesMap.containsKey(updateShiftWithActivityDTO.getId())) {
                ShiftViolatedRules currentShiftViolatedRules = shiftViolatedRulesMap.get(updateShiftWithActivityDTO.getId());
                List<ShiftWithActivityDTO> shiftForValidation = new ArrayList<>(existingShifts);
                shiftForValidation.removeIf(currentShiftWithActivityDTO -> currentShiftWithActivityDTO.isDeleted());
                WTAQueryResultDTO updateWtaQueryResultDTO = wtaQueryResultDTOS.stream().filter(wtaQueryResultDTO1 -> wtaQueryResultDTO1.isValidWorkTimeAgreement(shiftStartDate)).findFirst().orElseThrow(() -> new DataNotFoundException(MESSAGE_WTA_NOTFOUND));
                long expectedTimebank = timeBankService.getExpectedTimebankByDate(updateShiftWithActivityDTO, staffAdditionalInfoDTO);
                staffAdditionalInfoDTO.setStaffAge(getAgeByCPRNumberAndStartDate(staffAdditionalInfoDTO.getCprNumber(), shiftStartDate));
                PlanningPeriodDTO planningPeriod = planningPeriodDTOS.stream().filter(planningPeriodDTO -> shiftStartDate.equals(planningPeriodDTO.getStartDate()) || shiftStartDate.equals(planningPeriodDTO.getEndDate()) || (planningPeriodDTO.getStartDate().isBefore(shiftStartDate) && planningPeriodDTO.getEndDate().isAfter(shiftStartDate))).findFirst().get();
                Map<BigInteger, StaffWTACounter> staffWTACounterMap = staffWTACounters.stream().filter(staffWTACounter -> staffWTACounter.getStartDate().equals(planningPeriod.getStartDate()) && staffWTACounter.getEndDate().equals(planningPeriod.getEndDate())).collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, Function.identity()));
                RuleTemplateSpecificInfo ruleTemplateSpecificInfo = new RuleTemplateSpecificInfo(new ArrayList<>(shiftForValidation), updateShiftWithActivityDTO, timeSlotWrapperMap, phase.getId(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), new HashMap<>(), dayTypeDTOMap,  expectedTimebank, activityWrapperMap, staffAdditionalInfoDTO.getStaffAge(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), lastPlanningPeriod.getEndDate(), expertiseNightWorkerSetting, isNotNull(nightWorker) ? nightWorker.isNightWorker() : false, phase.getPhaseEnum());
                for (WTABaseRuleTemplate ruleTemplate : updateWtaQueryResultDTO.getRuleTemplates()) {
                    if(shiftViolatedRulesMap.get(updateShiftWithActivityDTO.getId()).getBreakedRuleTemplateIds().contains(ruleTemplate.getId())) {
                        updateStaffWTACounter(ObjectMapperUtils.copyPropertiesByMapper(updateShiftWithActivityDTO, Shift.class), currentShiftViolatedRules, ruleTemplate, staffWTACounterMap, ruleTemplateSpecificInfo);
                    }
                }
            }
        }
    }

    private ExpertiseNightWorkerSetting getExpertiseNightWorkerSetting(Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        ExpertiseNightWorkerSetting expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndUnitId(staffAdditionalInfoDTO.getEmployment().getExpertise().getId(), shift.getUnitId());
        if (expertiseNightWorkerSetting == null) {
            expertiseNightWorkerSetting = expertiseNightWorkerSettingRepository.findByExpertiseIdAndDeletedFalseAndCountryIdExistsTrue(staffAdditionalInfoDTO.getEmployment().getExpertise().getId());
        }
        return expertiseNightWorkerSetting;
    }

    private DateTimeInterval getIntervalByShifts(WTAQueryResultDTO wtaQueryResultDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, DateTimeInterval planningPeriodInterval, DateTimeInterval intervalByRuleTemplates, List<ShiftWithActivityDTO> shifts) {
        for (ShiftWithActivityDTO updateShiftWithActivityDTO : shifts) {
            intervalByRuleTemplates.addInterval(getIntervalByRuleTemplates(updateShiftWithActivityDTO, wtaQueryResultDTO.getRuleTemplates(), activityWrapperMap, planningPeriodInterval.getEndLocalDate()));
        }
        return intervalByRuleTemplates;
    }

    /*private void updateShiftLengthWTARuletemplate(Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap, ShiftWithActivityDTO shiftWithActivityDTO, List<PlanningPeriodDTO> planningPeriodDTOS, List<StaffWTACounter> staffWTACounters, PlanningPeriod lastPlanningPeriod, Map<Long, DayTypeDTO> dayTypeDTOMap, Map<String, TimeSlotWrapper> timeSlotWrapperMap, ExpertiseNightWorkerSetting expertiseNightWorkerSetting, NightWorker nightWorker, Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap, Map<Date, Phase> phaseMapByDate) {
        if(shiftViolatedRulesMap.containsKey(shiftWithActivityDTO.getId()) && shift.isDeleted()){
            Iterable<WTABaseRuleTemplate> wtaBaseRuleTemplates = wtaBaseRuleTemplateMongoRepository.findAllById(shiftViolatedRulesMap.get(shiftWithActivityDTO.getId()).getBreakedRuleTemplateIds());
            ShiftViolatedRules shiftViolatedRules = shiftViolatedRulesMap.get(shiftWithActivityDTO.getId());
            for (WTABaseRuleTemplate wtaBaseRuleTemplate : wtaBaseRuleTemplates) {
                LocalDate shiftStartDate = asLocalDate(shiftWithActivityDTO.getStartDate());
                PlanningPeriodDTO planningPeriod = planningPeriodDTOS.stream().filter(planningPeriodDTO -> shiftStartDate.equals(planningPeriodDTO.getStartDate()) || shiftStartDate.equals(planningPeriodDTO.getEndDate()) || (planningPeriodDTO.getStartDate().isBefore(shiftStartDate) && planningPeriodDTO.getEndDate().isAfter(shiftStartDate))).findFirst().get();
                Map<BigInteger, StaffWTACounter> staffWTACounterMap = staffWTACounters.stream().filter(staffWTACounter -> staffWTACounter.getStartDate().equals(planningPeriod.getStartDate()) && staffWTACounter.getEndDate().equals(planningPeriod.getEndDate())).collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, Function.identity()));
                Phase phase = phaseMapByDate.get(shiftWithActivityDTO.getStartDate());
                RuleTemplateSpecificInfo ruleTemplateSpecificInfo = new RuleTemplateSpecificInfo(newArrayList(), shiftWithActivityDTO, timeSlotWrapperMap, phase.getId(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), new HashMap<>(), dayTypeDTOMap, 0l, activityWrapperMap, staffAdditionalInfoDTO.getStaffAge(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), lastPlanningPeriod.getEndDate(), expertiseNightWorkerSetting, isNotNull(nightWorker) ? nightWorker.isNightWorker() : false, phase.getPhaseEnum());
                if(wtaBaseRuleTemplate instanceof ShiftLengthWTATemplate && shiftViolatedRulesMap.get(shiftWithActivityDTO.getId()).getBreakedRuleTemplateIds().contains(wtaBaseRuleTemplate.getId())){
                    updateStaffWTACounter(shift, shiftViolatedRules, wtaBaseRuleTemplate, staffWTACounterMap, ruleTemplateSpecificInfo);
                }
            }
        }
    }*/

    private void updateStaffWTACounter(Shift shift, ShiftViolatedRules shiftViolatedRules, WTABaseRuleTemplate wtaBaseRuleTemplate, Map<BigInteger, StaffWTACounter> staffWTACounterMap, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        if(staffWTACounterMap.containsKey(wtaBaseRuleTemplate.getId())) {
            StaffWTACounter staffWTACounter = staffWTACounterMap.get(wtaBaseRuleTemplate.getId());
            if(shift.isDeleted()){
                staffWTACounter.setCount(staffWTACounter.getCount()+1);
                shiftViolatedRules.getWorkTimeAgreements().removeIf(workTimeAgreementRuleViolation -> workTimeAgreementRuleViolation.getRuleTemplateId().equals(wtaBaseRuleTemplate.getId()));
            }else {
                wtaBaseRuleTemplate.validateRules(ruleTemplateSpecificInfo);
                if(!ruleTemplateSpecificInfo.isWTARuletemplateBroken(wtaBaseRuleTemplate.getId())){
                    staffWTACounter.setCount(staffWTACounter.getCount()+1);
                    shiftViolatedRules.getWorkTimeAgreements().removeIf(workTimeAgreementRuleViolation -> workTimeAgreementRuleViolation.getRuleTemplateId().equals(wtaBaseRuleTemplate.getId()));
                }
            }
            if(isCollectionEmpty(shiftViolatedRules.getWorkTimeAgreements())){
                shiftViolatedRules.getEscalationReasons().remove(WORK_TIME_AGREEMENT);
            }
        }
    }

}
