package com.kairos.service.shift;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityShiftStatusSettings;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.access_permission.StaffAccessGroupDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.staff.employment.StaffEmploymentUnitDataWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeCalaculationType;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.reason_code.ReasonCodeRequiredState;
import com.kairos.enums.shift.ShiftEscalationReason;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.shift.ShiftViolatedRules;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.staffing_level.StaffingLevelActivityRank;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.shift.ShiftViolatedRulesMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelActivityRankRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.persistence.repository.unit_settings.TimeAttendanceGracePeriodRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.*;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.staffing_level.StaffingLevelService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.commons.utils.ObjectUtils.getHoursByMinutes;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.TimeTypes.WORKING_TYPE;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getIntervalByRuleTemplates;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getValidDays;
import static java.util.Collections.singletonList;

/**
 * @author pradeep
 * @date - 10/5/18
 */
@Component
public class ShiftValidatorService {

    private static final Logger logger = LoggerFactory.getLogger(ShiftValidatorService.class);

    @Inject
    private PlanningPeriodMongoRepository planningPeriodMongoRepository;
    @Inject
    private StaffWTACounterRepository wtaCounterRepository;
    @Inject
    private
    ShiftMongoRepository shiftMongoRepository;
    @Inject
    private TimeBankRepository timeBankRepository;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private TimeAttendanceGracePeriodRepository timeAttendanceGracePeriodRepository;
    @Inject
    private StaffActivitySettingRepository staffActivitySettingRepository;
    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject
    private StaffingLevelActivityRankRepository staffingLevelActivityRankRepository;
    @Inject
    private PhaseSettingsRepository phaseSettingsRepository;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private StaffingLevelService staffingLevelService;

    @Inject
    private ShiftStateService shiftStateService;
    @Inject
    private MongoSequenceRepository mongoSequenceRepository;
    @Inject
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private PhaseService phaseService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ShiftViolatedRulesMongoRepository shiftViolatedRulesMongoRepository;


    private static ExceptionService exceptionService;

    @Autowired
    public void setExceptionService(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    public static void throwException(String exception, Object... param) {
        exceptionService.invalidRequestException(exception, param);
    }

    public static String convertMessage(String exception, Object... param) {
        return exceptionService.convertMessage(exception, param);
    }


    public void validateGracePeriod(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId, ShiftDTO staffShiftDTO) {
        String timeZone = userIntegrationService.getTimeZoneByUnitId(unitId);
        DateTimeInterval graceInterval = null;
        Phase phase = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
        if (validatedByStaff) {
            graceInterval = getGracePeriodInterval(phase, shiftDTO.getActivities().get(0).getStartDate(), validatedByStaff);
        } else {
            if (staffShiftDTO.getValidated() == null) {
                exceptionService.invalidRequestException("message.shift.cannot.validated");
            }
            graceInterval = getGracePeriodInterval(phase, shiftDTO.getActivities().get(0).getStartDate(), validatedByStaff);
        }
        if (!graceInterval.contains(DateUtils.getDateFromTimeZone(timeZone))) {
            exceptionService.invalidRequestException("message.shift.cannot.update");
        }
    }

    public DateTimeInterval getGracePeriodInterval(Phase phase, Date date, boolean forStaff) {
        ZonedDateTime startDate = DateUtils.asZoneDateTime(date);
        ZonedDateTime endDate;
        if (forStaff) {
            endDate = startDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(phase.getGracePeriodByStaff()).plusDays(1);
        } else {
            endDate = startDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(phase.getGracePeriodByStaff() + phase.getGracePeriodByManagement()).plusDays(1);
        }
        return new DateTimeInterval(startDate, endDate);
    }


    public ShiftWithViolatedInfoDTO validateShiftWithActivity(Phase phase, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift oldShift, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean byUpdate, boolean byTandAPhase) {
        if (wtaQueryResultDTO.getEndDate() != null && wtaQueryResultDTO.getEndDate().isBefore(asLocalDate(shift.getEndDate()))) {
            exceptionService.actionNotPermittedException("message.wta.expired-unit");
        }
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), asLocalDate(shift.getStartDate()));
        if (planningPeriod == null) {
            exceptionService.actionNotPermittedException("message.shift.planning.period.exits", shift.getStartDate());
        }
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(planningPeriod, phase, shift, wtaQueryResultDTO, staffAdditionalInfoDTO, activityWrapperMap);
        List<ActivityRuleViolation> activityRuleViolations = validateTimingOfActivity(shift, new ArrayList<>(activityWrapperMap.keySet()), activityWrapperMap);
        ruleTemplateSpecificInfo.getViolatedRules().getActivities().addAll(activityRuleViolations);
        validateAbsenceReasonCodeRule(activityWrapperMap, shift, ruleTemplateSpecificInfo);
        Specification<ShiftWithActivityDTO> activitySkillSpec = new StaffAndSkillSpecification(staffAdditionalInfoDTO.getSkills(), ruleTemplateSpecificInfo, exceptionService);
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffAdditionalInfoDTO.getEmployment().getExpertise(), ruleTemplateSpecificInfo);
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTO.getRuleTemplates());
        Specification<ShiftWithActivityDTO> activitySpecification = activityExpertiseSpecification.and(activitySkillSpec).and(wtaRulesSpecification);
        if (byUpdate) {
            Specification<ShiftWithActivityDTO> activityPhaseSettingSpecification = new ActivityPhaseSettingSpecification(staffAdditionalInfoDTO, phase, activityWrapperMap.values(), oldShift);
            activitySpecification = activitySpecification.and(activityPhaseSettingSpecification);
        } else {
            Specification<ShiftWithActivityDTO> staffEmploymentSpecification = new StaffEmploymentSpecification(phase, staffAdditionalInfoDTO);
            activitySpecification = activitySpecification.and(staffEmploymentSpecification);
        }
        if (!byTandAPhase) {
            Specification<ShiftWithActivityDTO> shiftTimeLessThan = new ShiftStartTimeLessThan();
            activitySpecification = activitySpecification.and(shiftTimeLessThan);
        }
        List<Long> dayTypeIds = shift.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getActivity().getRulesActivityTab().getDayTypes().stream()).collect(Collectors.toList());
        if (isCollectionNotEmpty(dayTypeIds)) {
            Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
            Set<DayOfWeek> validDays = getValidDays(dayTypeDTOMap, dayTypeIds);
            Specification<ShiftWithActivityDTO> activityDayTypeSpec = new DayTypeSpecification(validDays, shift.getStartDate());
            activitySpecification = activitySpecification.and(activityDayTypeSpec);
        }
        activitySpecification.validateRules(shift);


        //ruleTemplateSpecificInfo.getViolatedRules().setActivities(activityRuleViolations);
        return new ShiftWithViolatedInfoDTO(ruleTemplateSpecificInfo.getViolatedRules());
    }

    private void validateAbsenceReasonCodeRule(Map<BigInteger, ActivityWrapper> activityWrapperMap, ShiftWithActivityDTO shift, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        for (ShiftActivityDTO shiftActivity : shift.getActivities()) {
            Activity activity = activityWrapperMap.get(shiftActivity.getActivityId()).getActivity();
            ActivityRuleViolation activityRuleViolation = null;
            if (activity.getRulesActivityTab().isReasonCodeRequired() && activity.getRulesActivityTab().getReasonCodeRequiredState().
                    equals(ReasonCodeRequiredState.MANDATORY) && !Optional.ofNullable(shiftActivity.getAbsenceReasonCodeId()).isPresent()) {

                activityRuleViolation = ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(activity.getId())).findAny().orElse(null);
                if (activityRuleViolation == null) {
                    activityRuleViolation = new ActivityRuleViolation(activity.getId(), activity.getName(), 0, singletonList(exceptionService.
                            convertMessage("message.shift.reasoncode.required", activity.getId())));
                    ruleTemplateSpecificInfo.getViolatedRules().getActivities().add(activityRuleViolation);
                } else {
                    ruleTemplateSpecificInfo.getViolatedRules().getActivities().stream().filter(k -> k.getActivityId().equals(activity.getId())).findAny().get().getErrorMessages().add(exceptionService.
                            convertMessage("message.shift.reasoncode.required", activity.getId()));
                }

            }
        }

    }

    private List<ActivityRuleViolation> validateTimingOfActivity(ShiftWithActivityDTO shiftDTO, List<BigInteger> activityIds, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        List<StaffActivitySetting> staffActivitySettings = staffActivitySettingRepository.findByStaffIdAndActivityIdInAndDeletedFalse(shiftDTO.getStaffId(), activityIds);
        Map<BigInteger, StaffActivitySetting> staffActivitySettingMap = staffActivitySettings.stream().collect(Collectors.toMap(StaffActivitySetting::getActivityId, v -> v));
        Map<BigInteger, ShiftTimeDetails> shiftTimeDetailsMap = new HashMap<>();
        shiftDTO.getActivities().forEach(shiftActivity -> shiftTimeDetailsMap.put(shiftActivity.getActivityId(), prepareShiftTimeDetails(shiftActivity, shiftTimeDetailsMap)));
        List<ActivityRuleViolation> activityRuleViolations = new ArrayList<>();
        shiftTimeDetailsMap.forEach((activityId, shiftTimeDetails) -> {
            List<String> errorMessages = new ArrayList<>();
            Short shortestTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getRulesActivityTab().getShortestTime() : staffActivitySettingMap.get(activityId).getShortestTime();
            Short longestTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getRulesActivityTab().getLongestTime() : staffActivitySettingMap.get(activityId).getLongestTime();
            LocalTime earliestStartTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getRulesActivityTab().getEarliestStartTime() : staffActivitySettingMap.get(activityId).getEarliestStartTime();
            LocalTime latestStartTime = staffActivitySettingMap.get(activityId) == null ? activityWrapperMap.get(activityId).getActivity().getRulesActivityTab().getLatestStartTime() : staffActivitySettingMap.get(activityId).getLatestStartTime();
            if (shortestTime != null && shiftTimeDetails.getTotalTime() < shortestTime) {
                errorMessages.add(exceptionService.convertMessage("error.shift.duration.less_than.shortest_time", getHoursByMinutes(shortestTime)));
            }
            if (longestTime != null && shiftTimeDetails.getTotalTime() > longestTime) {
                errorMessages.add(exceptionService.convertMessage("error.shift.duration_exceeds_longest_time", getHoursByMinutes(longestTime)));
            }
            if (earliestStartTime != null && earliestStartTime.isAfter(shiftTimeDetails.getActivityStartTime())) {
                errorMessages.add(exceptionService.convertMessage("error.start_time.greater_than.earliest_time", earliestStartTime));
            }
            if (latestStartTime != null && latestStartTime.isBefore(shiftTimeDetails.getActivityStartTime())) {
                errorMessages.add(exceptionService.convertMessage("error.start_time.less_than.latest_time", latestStartTime));
            }
            if (!errorMessages.isEmpty()) {
                Activity activity = activityWrapperMap.get(activityId).getActivity();
                activityRuleViolations.add(new ActivityRuleViolation(activityId, activity.getName(), 0, errorMessages));
            }
        });
        return activityRuleViolations;
    }

    private ShiftTimeDetails prepareShiftTimeDetails(ShiftActivityDTO shiftActivity, Map<BigInteger, ShiftTimeDetails> shiftTimeDetailsMap) {
        Long shiftDurationInMinute = new DateTimeInterval(shiftActivity.getStartDate(), shiftActivity.getEndDate()).getMinutes();
        ShiftTimeDetails shiftTimeDetails = shiftTimeDetailsMap.get(shiftActivity.getActivityId());
        if (shiftTimeDetails == null) {
            shiftTimeDetails = new ShiftTimeDetails(shiftActivity.getActivityId(), DateUtils.asLocalTime(shiftActivity.getStartDate()), shiftDurationInMinute.shortValue());
        } else {
            shiftTimeDetails.setTotalTime((short) (shiftDurationInMinute.shortValue() + shiftTimeDetails.getTotalTime()));
        }
        shiftTimeDetailsMap.put(shiftActivity.getActivityId(), shiftTimeDetails);
        return shiftTimeDetails;
    }

    private RuleTemplateSpecificInfo getRuleTemplateSpecificInfo(PlanningPeriod planningPeriod, Phase phase, ShiftWithActivityDTO shift, WTAQueryResultDTO wtaQueryResultDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        logger.info("Current phase is " + phase.getName() + " for date " + new DateTime(shift.getStartDate()));
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getEmployment().getId(), DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()), staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff());
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(staffAdditionalInfoDTO.getUnitId());
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shift, wtaQueryResultDTO.getRuleTemplates(), activityWrapperMap, lastPlanningPeriod.getEndDate());
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmployment(shift.getEmploymentId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()));
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdAndBeforeDate(staffAdditionalInfoDTO.getEmployment().getId(), shift.getStartDate());
        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, sc -> sc.getCount()));
        Date endTimeOfInterval = DateUtils.getStartOfTheDay(DateUtils.asDate(DateUtils.asZoneDateTime(shift.getEndDate()).plusDays(1)));
        Interval interval = new Interval(DateUtils.getLongFromLocalDate(staffAdditionalInfoDTO.getEmployment().getStartDate()),
                staffAdditionalInfoDTO.getEmployment().getEndDate() == null ? endTimeOfInterval.getTime() : DateUtils.getLongFromLocalDate(staffAdditionalInfoDTO.getEmployment().getEndDate()));
        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = new EmploymentWithCtaDetailsDTO(staffAdditionalInfoDTO.getEmployment().getId(), staffAdditionalInfoDTO.getEmployment().getTotalWeeklyMinutes(), staffAdditionalInfoDTO.getEmployment().getWorkingDaysInWeek(),
                staffAdditionalInfoDTO.getEmployment().getStartDate(), staffAdditionalInfoDTO.getEmployment().getEndDate() != null ? staffAdditionalInfoDTO.getEmployment().getEndDate() : null, staffAdditionalInfoDTO.getEmployment().getTotalWeeklyHours());
        Set<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(shift.getUnitId(), interval.getStart().toDate(), interval.getEnd().toDate());
        int totalTimeBank = -timeBankCalculationService.calculateTimeBankForInterval(planningPeriodIntervals, interval, employmentWithCtaDetailsDTO, false, dailyTimeBankEntries, false);
        Map<String, TimeSlotWrapper> timeSlotWrapperMap = staffAdditionalInfoDTO.getTimeSlotSets().stream().collect(Collectors.toMap(TimeSlotWrapper::getName, v -> v));
        Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        return new RuleTemplateSpecificInfo(new ArrayList<>(shifts), shift, timeSlotWrapperMap, phase.getId(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), staffWTACounterMap, dayTypeDTOMap, staffAdditionalInfoDTO.getUserAccessRoleDTO(), totalTimeBank, activityWrapperMap, staffAdditionalInfoDTO.getStaffAge(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getChildCareDays(), staffAdditionalInfoDTO.getSeniorAndChildCareDays().getSeniorDays(), lastPlanningPeriod.getEndDate());
    }

    private RuleTemplateSpecificInfo getRuleTemplateSpecificInfo(PlanningPeriodDTO planningPeriod, ShiftWithActivityDTO shift, WTAQueryResultDTO wtaQueryResultDTO, StaffEmploymentDetails staffEmploymentDetails, Map<BigInteger, ActivityWrapper> activityWrapperMap, StaffEmploymentUnitDataWrapper dataWrapper, List<ShiftWithActivityDTO> newCreatedShiftWithActivityDTOs) {
        logger.info("Current phase is " + planningPeriod.getCurrentPhase() + " for date " + new DateTime(shift.getStartDate()));
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffEmploymentDetails.getId(), DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()), dataWrapper.getUser().getStaff());
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(dataWrapper.getUnitId());
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shift, wtaQueryResultDTO.getRuleTemplates(), activityWrapperMap, lastPlanningPeriod.getEndDate());
        List<ShiftWithActivityDTO> shifts;
        if (newCreatedShiftWithActivityDTOs.isEmpty()) {
            shifts = shiftMongoRepository.findAllShiftsBetweenDurationByEmployment(shift.getEmploymentId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()));
            newCreatedShiftWithActivityDTOs.addAll(shifts);
        } else {
            shifts = newCreatedShiftWithActivityDTOs;
        }
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByEmploymentIdAndBeforeDate(staffEmploymentDetails.getId(), shift.getStartDate());
        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, sc -> sc.getCount()));
        Date endTimeOfInterval = DateUtils.getStartOfTheDay(DateUtils.asDate(DateUtils.asZoneDateTime(shift.getEndDate()).plusDays(1)));
        Interval interval = new Interval(DateUtils.getLongFromLocalDate(staffEmploymentDetails.getStartDate()),
                staffEmploymentDetails.getEndDate() == null ? endTimeOfInterval.getTime() : DateUtils.getLongFromLocalDate(staffEmploymentDetails.getEndDate()));

        EmploymentWithCtaDetailsDTO employmentWithCtaDetailsDTO = new EmploymentWithCtaDetailsDTO(staffEmploymentDetails.getId(), staffEmploymentDetails.getTotalWeeklyMinutes(), staffEmploymentDetails.getWorkingDaysInWeek(),
                staffEmploymentDetails.getStartDate(), staffEmploymentDetails.getEndDate(), staffEmploymentDetails.getTotalWeeklyMinutes());

        Set<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(shift.getUnitId(), interval.getStart().toDate(), interval.getEnd().toDate());

        int totalTimeBank = -timeBankCalculationService.calculateTimeBankForInterval(planningPeriodIntervals, interval, employmentWithCtaDetailsDTO, false, dailyTimeBankEntries, false);
        Map<String, TimeSlotWrapper> timeSlotWrapperMap = dataWrapper.getTimeSlotWrappers().stream().collect(Collectors.toMap(TimeSlotWrapper::getName, v -> v));
        Map<Long, DayTypeDTO> dayTypeDTOMap = dataWrapper.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));

        return new RuleTemplateSpecificInfo(new ArrayList<>(shifts), shift, timeSlotWrapperMap, planningPeriod.getCurrentPhaseId(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), staffWTACounterMap, dayTypeDTOMap, dataWrapper.getUser(), totalTimeBank, activityWrapperMap, dataWrapper.getStaffAge(), dataWrapper.getSeniorAndChildCareDays().getChildCareDays(), dataWrapper.getSeniorAndChildCareDays().getSeniorDays(), lastPlanningPeriod.getEndDate());
    }


    public List<String> validateShiftWhileCopy(StaffEmploymentUnitDataWrapper dataWrapper, ShiftWithActivityDTO shiftWithActivityDTO, StaffEmploymentDetails staffEmploymentDetails, List<WTAQueryResultDTO> wtaQueryResultDTOS, PlanningPeriodDTO planningPeriod, Map<BigInteger, ActivityWrapper> activityWrapperMap, List<ShiftWithActivityDTO> newCreatedShiftWithActivityDTOs) {
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(planningPeriod, shiftWithActivityDTO, wtaQueryResultDTOS.get(0), staffEmploymentDetails, activityWrapperMap, dataWrapper, newCreatedShiftWithActivityDTOs);
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTOS.get(0).getRuleTemplates());
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffEmploymentDetails.getExpertise(), ruleTemplateSpecificInfo);
        Specification<ShiftWithActivityDTO> activitySpecification = activityExpertiseSpecification
                .and(wtaRulesSpecification);
        List<String> voilatedRules;
        activitySpecification.validateRules(shiftWithActivityDTO);
        voilatedRules = filterVoilatedRules(ruleTemplateSpecificInfo.getViolatedRules());
        return voilatedRules;
    }


    public void validateStatusOfShiftActivity(Shift shift) {
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            boolean notValid = shiftActivity.getStatus().contains(ShiftStatus.FIX) || shiftActivity.getStatus().contains(ShiftStatus.PUBLISH) || shiftActivity.getStatus().contains(ShiftStatus.LOCK) || shiftActivity.getStatus().contains(ShiftStatus.APPROVE);
            if (notValid) {
                exceptionService.actionNotPermittedException("message.shift.state.update", shiftActivity.getStatus());
            }
        }

    }

    public static List<ShiftWithActivityDTO> filterShiftsByPlannedTypeAndTimeTypeIds(List<ShiftWithActivityDTO> shifts, Set<BigInteger> timeTypeIds, Set<BigInteger> plannedTimeIds) {
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = new ArrayList<>();
        shifts.forEach(shift -> {
            boolean isValidShift = (CollectionUtils.isNotEmpty(timeTypeIds) && CollectionUtils.containsAny(timeTypeIds, shift.getActivitiesTimeTypeIds())) && (CollectionUtils.isNotEmpty(plannedTimeIds) && CollectionUtils.containsAny(plannedTimeIds, shift.getActivitiesPlannedTimeIds()));
            if (isValidShift) {
                shiftQueryResultWithActivities.add(shift);
            }

        });
        return shiftQueryResultWithActivities;
    }


    public void verifyRankAndStaffingLevel(List<ShiftActivityDTO> shiftActivities, Long unitId, List<ActivityWrapper> activities, Phase phase, UserAccessRoleDTO userAccessRoleDTO) {
        if (!shiftActivities.isEmpty()) {
            PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(unitId, phase.getId());
            if (!phaseSettings.isManagementEligibleForOverStaffing() || !phaseSettings.isManagementEligibleForUnderStaffing() || !phaseSettings.isStaffEligibleForOverStaffing() || !phaseSettings.isStaffEligibleForUnderStaffing()) {
                Activity existing = activities.stream().filter(k -> k.getActivity().getId().equals(shiftActivities.get(0).getActivityId())).findFirst().get().getActivity();
                Activity arrived = activities.stream().filter(k -> k.getActivity().getId().equals(shiftActivities.get(1).getActivityId())).findFirst().get().getActivity();
                if (existing.getRulesActivityTab().isEligibleForStaffingLevel() && arrived.getRulesActivityTab().isEligibleForStaffingLevel()) {
                    Date startDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftActivities.get(0).getStartDate()).truncatedTo(ChronoUnit.DAYS));
                    Date endDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftActivities.get(0).getEndDate()).truncatedTo(ChronoUnit.DAYS));
                    List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId, startDate, endDate);
                    if (!Optional.ofNullable(staffingLevels).isPresent() || staffingLevels.isEmpty()) {
                        exceptionService.actionNotPermittedException("message.staffingLevel.absent");
                    }
                    List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(shiftActivities.get(0).getStartDate(), shiftActivities.get(0).getEndDate(), unitId);
                    StaffingLevelActivityRank rankOfExisting = staffingLevelActivityRankRepository.findByStaffingLevelDateAndActivityId(asLocalDate(shiftActivities.get(0).getStartDate()), shiftActivities.get(0).getActivityId());
                    StaffingLevelActivityRank rankOfReplaced = staffingLevelActivityRankRepository.findByStaffingLevelDateAndActivityId(asLocalDate(shiftActivities.get(1).getStartDate()), shiftActivities.get(1).getActivityId());
                    String staffingLevelForExistingActivity = getStaffingLevel(existing, staffingLevels, shifts, false);
                    String staffingLevelForReplacedActivity = getStaffingLevel(arrived, staffingLevels, shifts, true);
                    boolean checkForStaff = userAccessRoleDTO.getStaff();
                    boolean checkForManagement = userAccessRoleDTO.getManagement();
                    if (rankOfExisting != null && rankOfReplaced != null) {
                        logger.info("validating ranking of activities");
                        boolean phaseSettingsValidated = false;
                        if ((checkForStaff && checkForManagement) && (!phaseSettings.isStaffEligibleForUnderStaffing() && !phaseSettings.isManagementEligibleForUnderStaffing())) {
                            phaseSettingsValidated = true;
                        } else if (checkForStaff ? !phaseSettings.isStaffEligibleForUnderStaffing() : !phaseSettings.isManagementEligibleForUnderStaffing()) {
                            phaseSettingsValidated = true;
                        }
                        if (phaseSettingsValidated && (rankOfExisting.getRank() > rankOfReplaced.getRank() && UNDERSTAFFING.equals(staffingLevelForExistingActivity) && UNDERSTAFFING.equals(staffingLevelForReplacedActivity))
                                || (rankOfExisting.getRank() > rankOfReplaced.getRank() && BALANCED.equals(staffingLevelForReplacedActivity))
                                || (BALANCED.equals(staffingLevelForReplacedActivity) && BALANCED.equals(staffingLevelForExistingActivity))
                                || (staffingLevelForReplacedActivity == null && rankOfExisting.getRank() > rankOfReplaced.getRank())
                                ) {
                            logger.info("shift can be replaced");
                        } else {
                            exceptionService.actionNotPermittedException("shift.can.not.move", staffingLevelForReplacedActivity);
                        }

                    }
                }
            }
        }
    }

    private String getStaffingLevel(Activity activity, List<StaffingLevel> staffingLevels, List<Shift> shifts, boolean addShift) {
        String staffingLevelStatus = null;
        if (activity.getRulesActivityTab().isEligibleForStaffingLevel()) {
            for (StaffingLevel staffingLevel : staffingLevels) {
                List<StaffingLevelInterval> staffingLevelIntervals = (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_DAY_CALCULATION) ||
                        activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(FULL_WEEK)) ? staffingLevel.getAbsenceStaffingLevelInterval() : staffingLevel.getPresenceStaffingLevelInterval();
                for (StaffingLevelInterval staffingLevelInterval : staffingLevelIntervals) {
                    int shiftsCount = 0;
                    Optional<StaffingLevelActivity> staffingLevelActivity = staffingLevelInterval.getStaffingLevelActivities().stream().filter(sa -> sa.getActivityId().equals(activity.getId())).findFirst();
                    if (staffingLevelActivity.isPresent()) {
                        ZonedDateTime startDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevelInterval.getStaffingLevelDuration().getFrom());
                        ZonedDateTime endDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevelInterval.getStaffingLevelDuration().getTo());
                        DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
                        boolean overlapped = false;
                        for (Shift shift : shifts) {
                            if (shift.getActivities().get(0).getActivityId().equals(activity.getId()) && interval.overlaps(shift.getInterval())) {
                                shiftsCount++;
                                overlapped = true;
                            }
                        }
                        if (overlapped) {
                            shiftsCount = addShift ? shiftsCount + 1 : shiftsCount - 1;
                            if (shiftsCount > staffingLevelActivity.get().getMaxNoOfStaff()) {
                                staffingLevelStatus = OVERSTAFFING;
                                break;
                            } else if (shiftsCount < staffingLevelActivity.get().getMinNoOfStaff()) {
                                staffingLevelStatus = UNDERSTAFFING;
                                break;
                            } else {
                                staffingLevelStatus = BALANCED;
                            }
                        }
                    } else {
                        exceptionService.actionNotPermittedException("message.staffingLevel.activity", activity.getName());
                    }

                }
            }
        }
        return staffingLevelStatus;

    }

    public void verifyShiftActivities(Set<AccessGroupRole> roles, Long employmentTypeId, Map<BigInteger, com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue> phaseTemplateValue, ShiftActivityIdsDTO shiftActivityIdsDTO) {
        boolean staff = roles.contains(AccessGroupRole.STAFF);
        boolean management = roles.contains(AccessGroupRole.MANAGEMENT);
        phaseTemplateValue.forEach((k, v) -> {
            if (shiftActivityIdsDTO.getActivitiesToAdd().contains(k)) {
                if ((staff && !v.getEligibleEmploymentTypes().contains(employmentTypeId)) || (management && !v.isEligibleForManagement())) {
                    exceptionService.actionNotPermittedException("error.shift.not.authorised.phase");
                }
            }
            if (shiftActivityIdsDTO.getActivitiesToEdit().contains(k)) {
                if (!CollectionUtils.containsAny(v.getAllowedSettings().getCanEdit(), roles)) {
                    exceptionService.actionNotPermittedException("error.shift.not.editable.phase");
                }
            }
            if (shiftActivityIdsDTO.getActivitiesToDelete().contains(k)) {
                if ((management && !v.isManagementCanDelete()) || (staff && !v.isStaffCanDelete())) {
                    exceptionService.actionNotPermittedException("error.shift.not.deletable.phase");
                }
            }

        });
    }


    private List<String> filterVoilatedRules(ViolatedRulesDTO violatedRulesDTO) {
        List<String> messages = new ArrayList<>();
        if (violatedRulesDTO != null) {
            violatedRulesDTO.getActivities().forEach(activityRuleViolation -> {
                messages.addAll(activityRuleViolation.getErrorMessages());
            });
            violatedRulesDTO.getWorkTimeAgreements().forEach(workTimeAgreementRuleViolation -> {
                messages.add(workTimeAgreementRuleViolation.getName() + IS_BROKEN);
            });
        }
        return messages;
    }

    public void validateStaffingLevel(Phase phase, Shift shift, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean checkOverStaffing, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Date shiftStartDate = shift.getActivities().get(0).getStartDate();
        Date shiftEndDate = shift.getActivities().get(shift.getActivities().size() - 1).getEndDate();
        PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(shift.getUnitId(), phase.getId());
        if (!Optional.ofNullable(phaseSettings).isPresent()) {
            exceptionService.dataNotFoundException("message.phaseSettings.absent");
        }

        if (isVerificationRequired(checkOverStaffing, staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff(), staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement(),
                phaseSettings)) {
            Date startDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftStartDate).truncatedTo(ChronoUnit.DAYS));
            Date endDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftEndDate).truncatedTo(ChronoUnit.DAYS));
            List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.getStaffingLevelsByUnitIdAndDate(shift.getUnitId(), startDate, endDate);
            if (CollectionUtils.isEmpty(staffingLevels)) {
                exceptionService.actionNotPermittedException("message.staffingLevel.absent");
            }
            List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalseAndIdNotEqualTo(shiftStartDate, shiftEndDate, shift.getUnitId(), shift.getId());
            List<ShiftActivity> shiftActivities = shifts.stream().flatMap(curShift -> curShift.getActivities().stream()).collect(Collectors.toList());
            StaffingLevel staffingLevel = staffingLevels.get(0);
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                ActivityWrapper activityWrapper = activityWrapperMap.get(shiftActivity.getActivityId());
                if (activityWrapper.getActivity().getRulesActivityTab().isEligibleForStaffingLevel()) {
                    int lowerLimit = 0;
                    int upperLimit = 0;
                    if (ShiftType.PRESENCE.equals(shift.getShiftType())) {
                        List<StaffingLevelInterval> applicableIntervals = staffingLevel.getPresenceStaffingLevelInterval();
                        if (!DateUtils.getLocalDateFromDate(shiftActivity.getStartDate()).equals(DateUtils.getLocalDateFromDate(shiftActivity.getEndDate()))) {
                            lowerLimit = staffingLevelService.getLowerIndex(shiftActivity.getStartDate());
                            upperLimit = 95;
                            checkStaffingLevelInterval(lowerLimit, upperLimit, applicableIntervals, staffingLevel, shiftActivities, checkOverStaffing, shiftActivity);
                            lowerLimit = 0;
                            upperLimit = staffingLevelService.getUpperIndex(shiftActivity.getEndDate());
                            if (staffingLevels.size() < 2) {
                                exceptionService.actionNotPermittedException("message.staffingLevel.absent");
                            }
                            staffingLevel = staffingLevels.get(1);
                            applicableIntervals = staffingLevel.getPresenceStaffingLevelInterval();

                            checkStaffingLevelInterval(lowerLimit, upperLimit, applicableIntervals, staffingLevel, shiftActivities, checkOverStaffing, shiftActivity);

                        } else {
                            lowerLimit = staffingLevelService.getLowerIndex(shiftActivity.getStartDate());
                            upperLimit = staffingLevelService.getUpperIndex(shiftActivity.getEndDate());
                            checkStaffingLevelInterval(lowerLimit, upperLimit, applicableIntervals, staffingLevel, shiftActivities, checkOverStaffing, shiftActivity);
                        }
                    } else {
                        validateStaffingLevelForAbsenceTypeOfShift(staffingLevel, shiftActivity, checkOverStaffing, shiftActivities);
                    }
                }
            }
        }
    }


    private boolean isVerificationRequired(boolean checkOverStaffing, boolean staff, boolean management, PhaseSettings phaseSettings) {
        boolean result = false;
        if (staff && management) {
            result = checkOverStaffing ? !(phaseSettings.isManagementEligibleForOverStaffing() || phaseSettings.isStaffEligibleForOverStaffing()) : !(phaseSettings.
                    isManagementEligibleForUnderStaffing() || phaseSettings.isManagementEligibleForUnderStaffing());
        } else if (staff) {
            result = checkOverStaffing ? !phaseSettings.isStaffEligibleForOverStaffing() : !phaseSettings.isStaffEligibleForUnderStaffing();
        } else if (management) {
            result = checkOverStaffing ? !phaseSettings.isManagementEligibleForOverStaffing() : !phaseSettings.isManagementEligibleForUnderStaffing();
        }
        return result;
    }

    private void checkStaffingLevelInterval(int lowerLimit, int upperLimit, List<StaffingLevelInterval> applicableIntervals, StaffingLevel staffingLevel,
                                            List<ShiftActivity> shiftActivities, boolean checkOverStaffing, ShiftActivity shiftActivity) {
        Activity parentActivity = activityMongoRepository.findByChildActivityId(shiftActivity.getActivityId());
        ActivityDTO activity = null;
        if (isNull(parentActivity)) {
            activity = activityMongoRepository.findByIdAndChildActivityEligibleForStaffingLevelTrue(shiftActivity.getActivityId());
        }
        for (int currentIndex = lowerLimit; currentIndex <= upperLimit; currentIndex++) {
            int shiftsCount = 0;
            Optional<StaffingLevelActivity> staffingLevelActivity = applicableIntervals.get(currentIndex).getStaffingLevelActivities().stream().filter(sa -> sa.getActivityId().equals(shiftActivity.getActivityId())).findFirst();
            if (staffingLevelActivity.isPresent()) {
                ZonedDateTime startDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getStaffingLevelDuration().getFrom());
                ZonedDateTime endDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevel.getPresenceStaffingLevelInterval().get(currentIndex).getStaffingLevelDuration().getTo());
                DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
                for (ShiftActivity shiftActivityDB : shiftActivities) {
                    if (shiftActivityDB.getActivityId().equals(shiftActivity.getActivityId()) && interval.overlaps(shiftActivityDB.getInterval())) {
                        shiftsCount++;
                    } else if (activity != null && activity.getChildActivityIds().contains(shiftActivityDB.getActivityId())) {
                        shiftsCount++;
                    }
                }
                int totalCount = shiftsCount - (checkOverStaffing ? staffingLevelActivity.get().getMaxNoOfStaff() : staffingLevelActivity.get().getMinNoOfStaff());
                if ((checkOverStaffing && totalCount >= 0)) {
                    exceptionService.actionNotPermittedException("message.shift.overStaffing");
                }
                if (!checkOverStaffing && totalCount <= 0) {
                    exceptionService.actionNotPermittedException("message.shift.underStaffing");
                }

                if (isNotNull(parentActivity)) {
                    applicableIntervals.get(currentIndex).getStaffingLevelActivities().stream().forEach(staffingLevelActivityObj ->
                            {
                                if (staffingLevelActivityObj.getActivityId().equals(parentActivity.getId()) && staffingLevelActivityObj.getAvailableNoOfStaff() >= staffingLevelActivityObj.getMaxNoOfStaff()) {
                                    exceptionService.actionNotPermittedException("message.shift.overStaffing");
                                }
                            }
                    );
                }
            } else {
                exceptionService.actionNotPermittedException("message.staffingLevel.activity", shiftActivity.getActivityName());
            }
        }

    }

    public void validateShifts(List<ShiftDTO> shiftDTOS) {
        Date shiftsStartDate = shiftDTOS.get(0).getActivities().get(0).getStartDate();
        Date shiftsEndDate = shiftDTOS.get(shiftDTOS.size() - 1).getActivities().get(shiftDTOS.get(shiftDTOS.size() - 1).getActivities().size() - 1).getEndDate();
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationByEmploymentId(shiftDTOS.get(0).getEmploymentId(), shiftsStartDate, shiftsEndDate);
        if (shifts.stream().filter(shift -> !shift.getId().equals(shiftDTOS.get(0).getId())).findAny().isPresent()) {
            exceptionService.duplicateDataException("message.shift.date.startandend", shiftsStartDate, shiftsEndDate);
        }
    }

    public boolean validateAccessGroup(ActivityShiftStatusSettings activityShiftStatusSettings, StaffAccessGroupDTO
            staffAccessGroupDTO) {
        return activityShiftStatusSettings != null && staffAccessGroupDTO != null && CollectionUtils.containsAny(activityShiftStatusSettings.getAccessGroupIds(), staffAccessGroupDTO.getAccessGroupIds());
    }


    public List<ShiftActivityDTO> findShiftActivityToValidateStaffingLevel
            (List<ShiftActivity> existingShiftActivities, List<ShiftActivityDTO> arrivedShiftActivities) {
        List<ShiftActivityDTO> shiftActivities = new ArrayList<>();
        try {
            for (int i = 0; i < arrivedShiftActivities.size(); i++) {
                ShiftActivityDTO currentShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(arrivedShiftActivities.get(i), ShiftActivityDTO.class);
                ShiftActivityDTO existingShiftActivity = ObjectMapperUtils.copyPropertiesByMapper(existingShiftActivities.get(i), ShiftActivityDTO.class);
                if (!currentShiftActivity.getActivityId().equals(existingShiftActivity.getActivityId())) {
                    currentShiftActivity.setStartDate(existingShiftActivity.getStartDate());
                    shiftActivities.add(existingShiftActivity);
                    shiftActivities.add(currentShiftActivity);
                    break;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            //Intentionally left blank to avoid ArrayIndexOutOfBoundsException
        }
        return shiftActivities;
    }

    public ShiftWithViolatedInfoDTO validateShift(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId, String type) {
        UserAccessRoleDTO userAccessRoleDTO = userIntegrationService.getAccessOfCurrentLoggedInStaff();
        if (!userAccessRoleDTO.getStaff() && validatedByStaff) {
            exceptionService.actionNotPermittedException("message.shift.validation.access");
        } else if (!userAccessRoleDTO.getManagement() && !validatedByStaff) {
            exceptionService.actionNotPermittedException("message.shift.validation.access");
        }
        Phase actualPhases = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.TIME_ATTENDANCE.toString());
        ShiftState shiftState = null;
        //StaffAdditionalInfoDTO staffAdditionalInfoDTO = userIntegrationService.verifyUnitEmploymentOfStaff(DateUtils.asLocalDate(shiftDTO.getActivities().get(0).getStartDate()),shiftDTO.getStaffId(), type, shiftDTO.getEmploymentId(),Collections.emptySet());
        if (!validatedByStaff) {
            shiftState = shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhaseAndRole(shiftDTO.getShiftId(), shiftDTO.getShiftStatePhaseId(), AccessGroupRole.STAFF);
        }
        //    shiftValidatorService.validateGracePeriod(shiftDTO, validatedByStaff, unitId, staffShiftDTO);
        BigInteger shiftStateId = shiftDTO.getId();
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftService.updateShift(shiftDTO, type, true, !validatedByStaff);
        if (shiftWithViolatedInfoDTO.getViolatedRules().getActivities().isEmpty() && shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements().isEmpty()) {
            shiftDTO = validateShiftStateAfterValidatingWtaRule(shiftDTO, shiftState, validatedByStaff, actualPhases, shiftStateId);
            shiftDTO.setEscalationReasons(shiftWithViolatedInfoDTO.getShifts().get(0).getEscalationReasons());
            shiftDTO.setRestingMinutes(shiftWithViolatedInfoDTO.getShifts().get(0).getRestingMinutes());
            shiftWithViolatedInfoDTO.setShifts(newArrayList(shiftDTO));
        }
        return shiftWithViolatedInfoDTO;
    }

    public ShiftDTO validateShiftStateAfterValidatingWtaRule(ShiftDTO shiftDTO, ShiftState shiftState, Boolean
            validatedByStaff, Phase actualPhases, BigInteger shiftStateId) {
        shiftState = shiftStateMongoRepository.findOne(shiftStateId);
        if (shiftState == null) {
            Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
            if (shift != null) {
                shiftDTO.setId(null);
            }
        }
        if (shiftState != null) {
            shiftDTO.setId(shiftStateId);
            shiftDTO.setAccessGroupRole(shiftState.getAccessGroupRole());
            shiftDTO.setValidated(shiftState.getValidated());
            shiftDTO.setShiftStatePhaseId(shiftState.getShiftStatePhaseId());
            shiftDTO.setShiftId(shiftState.getShiftId());
        }
        shiftState = ObjectMapperUtils.copyPropertiesByMapper(shiftDTO, ShiftState.class);
        List<BigInteger> activityIds = shiftState.getActivities().stream().map(s -> s.getActivityId()).collect(Collectors.toList());
        List<ActivityWrapper> activities = activityMongoRepository.findActivitiesAndTimeTypeByActivityId(activityIds);
        Map<BigInteger, ActivityWrapper> activityWrapperMap = activities.stream().collect(Collectors.toMap(k -> k.getActivity().getId(), v -> v));
        shiftState.setShiftId(shiftDTO.getShiftId());
        shiftState.setStartDate(shiftState.getActivities().get(0).getStartDate());
        shiftState.setEndDate(shiftState.getActivities().get(shiftState.getActivities().size() - 1).getEndDate());
        shiftState.setValidated(LocalDate.now());
        shiftState.setShiftStatePhaseId(actualPhases.getId());
        shiftState.getActivities().forEach(activity -> {
            activity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName()));
            activity.setActivityName(activityWrapperMap.get(activity.getActivityId()).getActivity().getName());
        });
        shiftStateMongoRepository.save(shiftState);
        if (validatedByStaff) {
            shiftState.setAccessGroupRole(AccessGroupRole.MANAGEMENT);
            shiftState.setShiftStatePhaseId(actualPhases.getId());
            shiftState.setId(null);
            shiftState.setValidated(null);
            shiftState.getActivities().forEach(activity -> activity.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
            shiftStateMongoRepository.save(shiftState);
        }
        shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shiftState, ShiftDTO.class);
        if (validatedByStaff) {
            shiftDTO.setEditable(true);
        }
        shiftDTO.setDurationMinutes((int) shiftDTO.getInterval().getMinutes());

        return shiftDTO;

    }

    public void validateRealTimeShift(Long unitId, ShiftDTO shiftDTO, Map<String, Phase> phaseMap) {
        String timeZone = userIntegrationService.getTimeZoneByUnitId(unitId);
        ShiftState shiftState = shiftStateMongoRepository.findShiftStateByShiftIdAndActualPhase(shiftDTO.getShiftId(), phaseMap.get(PhaseDefaultName.REALTIME.toString()).getId());
        Map<BigInteger, ShiftActivity> activityMap = shiftState.getActivities().stream().filter(distinctByKey(a -> a.getId())).collect(Collectors.toMap(k -> k.getId(), v -> v));
        boolean realtime = phaseService.shiftEditableInRealtime(timeZone, phaseMap, shiftDTO.getActivities().get(0).getStartDate(), shiftDTO.getActivities().get(shiftDTO.getActivities().size() - 1).getEndDate());
        if (realtime) {
            shiftDTO.getActivities().forEach(shiftActivity -> {
                ShiftActivity shiftActivity1 = activityMap.get(shiftActivity.getId());
                if (shiftActivity1 != null &&
                        ((!shiftActivity.getStartDate().equals(shiftActivity1.getStartDate()) && shiftActivity.getStartDate().before(asDate(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(timeZone)))))
                                || (!shiftActivity.getEndDate().equals(shiftActivity1.getEndDate()) && shiftActivity.getEndDate().before(asDate(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(timeZone))))))) {
                    exceptionService.actionNotPermittedException("error.activity.startdate", shiftActivity.getActivityName());
                }
            });
        }
    }

    public boolean validateStaffDetailsAndShiftOverlapping(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftDTO
            shiftDTO, ActivityWrapper activityWrapper, boolean byTandAPhase) {
        Activity activity = activityWrapper.getActivity();
        boolean shiftOverlappedWithNonWorkingType = false;
        if (!Optional.ofNullable(activity).isPresent()) {
            exceptionService.invalidRequestException("message.activity.id", shiftDTO.getActivities().get(0).getActivityId());
        }
        if (staffAdditionalInfoDTO == null) {
            exceptionService.invalidRequestException("message.staff.notfound");
        }
        if (!Optional.ofNullable(staffAdditionalInfoDTO.getEmployment()).isPresent()) {
            exceptionService.actionNotPermittedException("message.employment.absent");
        }
        if (!staffAdditionalInfoDTO.getEmployment().isPublished()) {
            exceptionService.invalidRequestException("message.shift.not.published");
        }
        if (!staffAdditionalInfoDTO.getEmployment().isPublished()) {
            exceptionService.invalidRequestException("message.shift.not.published");
        }
        if (staffAdditionalInfoDTO.getUnitId() == null) {
            exceptionService.invalidRequestException("message.staff.unit", shiftDTO.getStaffId(), shiftDTO.getUnitId());
        }
        if (FULL_WEEK.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())) {
            // shiftDTO.setEndDate(asDate(shiftDTO.getShiftDate().plusDays(7)));
            if (TimeCalaculationType.MIDNIGHT_TO_MIDNIGHT_TYPE.equals((activityWrapper.getActivity().getTimeCalculationActivityTab().getFullWeekCalculationType()))) {
                shiftDTO.setEndDate(asDate(shiftDTO.getShiftDate().atTime(LocalTime.MAX)));
            } else {
                shiftDTO.setEndDate(asDate(shiftDTO.getShiftDate().plusDays(7)));
            }

        } else if (FULL_DAY_CALCULATION.equals(activityWrapper.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()))
            if (TimeCalaculationType.MIDNIGHT_TO_MIDNIGHT_TYPE.equals((activityWrapper.getActivity().getTimeCalculationActivityTab().getFullDayCalculationType()))) {
                shiftDTO.setEndDate(asDate(shiftDTO.getShiftDate().atTime(LocalTime.MAX)));
            } else {
                shiftDTO.setEndDate(asDate(shiftDTO.getShiftDate().plusDays(1)));
            }
        //As discussed with Arvind we remove the Check of cross organization overlapping functionality
        boolean shiftExists = shiftMongoRepository.existShiftsBetweenDurationByEmploymentIdAndTimeType(byTandAPhase ? shiftDTO.getShiftId() : shiftDTO.getId(), staffAdditionalInfoDTO.getEmployment().getId(), shiftDTO.getStartDate(), shiftDTO.getEndDate(), WORKING_TYPE);
        if (!shiftExists) {
            shiftExists = shiftMongoRepository.existShiftsBetweenDurationByEmploymentIdAndTimeType(shiftDTO.getId(), staffAdditionalInfoDTO.getEmployment().getId(), shiftDTO.getStartDate(), shiftDTO.getEndDate(), TimeTypes.NON_WORKING_TYPE);
            if (shiftExists && WORKING_TYPE.name().equals(activityWrapper.getTimeType()) && staffAdditionalInfoDTO.getUserAccessRoleDTO().getManagement()) {
                shiftExists = false;
                shiftOverlappedWithNonWorkingType = true;
            }
        }
        if (shiftExists) {
            exceptionService.invalidRequestException("message.shift.date.startandend", shiftDTO.getStartDate(), shiftDTO.getEndDate());
        }

        return shiftOverlappedWithNonWorkingType;
    }

    public boolean deleteDuplicateEntryOfShiftViolatedInfo() {
        List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAll();
        Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = new HashMap<>();
        List<ShiftViolatedRules> violatedRules = new ArrayList<>();
        for (ShiftViolatedRules shiftViolatedRules1 : shiftViolatedRules) {
            if (shiftViolatedRulesMap.containsKey(shiftViolatedRules1.getShiftId())) {
                violatedRules.add(shiftViolatedRules1);
            } else {
                shiftViolatedRulesMap.put(shiftViolatedRules1.getShiftId(), shiftViolatedRules1);
            }
        }
        logger.info("Duplicate remove entry count is " + violatedRules.size());
        shiftViolatedRulesMongoRepository.deleteAll(violatedRules);
        return true;
    }

    private void validateStaffingLevelForAbsenceTypeOfShift(StaffingLevel staffingLevel, ShiftActivity
            shiftActivity, boolean checkOverStaffing, List<ShiftActivity> shiftActivities) {
        if (CollectionUtils.isEmpty(staffingLevel.getAbsenceStaffingLevelInterval())) {
            exceptionService.actionNotPermittedException("message.staffingLevel.absent");
        }
        int shiftsCount = 0;
        Optional<StaffingLevelActivity> staffingLevelActivity = staffingLevel.getAbsenceStaffingLevelInterval().get(0).getStaffingLevelActivities().stream().filter(sa -> sa.getActivityId().equals(shiftActivity.getActivityId())).findFirst();
        if (!staffingLevelActivity.isPresent()) {
            exceptionService.actionNotPermittedException("message.staffingLevel.activity", shiftActivity.getActivityName());
        }
        for (ShiftActivity currentShiftActivity : shiftActivities) {
            if (currentShiftActivity.getActivityId().equals(shiftActivity.getActivityId())) {
                shiftsCount++;
            }
        }
        int totalCount = shiftsCount - (checkOverStaffing ? staffingLevelActivity.get().getMaxNoOfStaff() : staffingLevelActivity.get().getMinNoOfStaff());
        if ((checkOverStaffing && totalCount >= 0)) {
            exceptionService.actionNotPermittedException("message.shift.overStaffing");

        }
        if (!checkOverStaffing && totalCount <= 0) {
            exceptionService.actionNotPermittedException("message.shift.underStaffing");

        }
    }

    //This method is being used to check overlapping shift with full day and full week activity
    public void checkAbsenceTypeShift(ShiftDTO shiftDTO) {
        Date startDate;
        Date endDate;
        if (shiftDTO.getStartDate() != null && asLocalDate(shiftDTO.getEndDate()).isAfter(asLocalDate(shiftDTO.getStartDate()))) {
            startDate = DateUtils.getStartOfDay(shiftDTO.getStartDate());
            endDate = DateUtils.getEndOfDay(shiftDTO.getEndDate());
        } else {
            startDate = asDateStartOfDay(shiftDTO.getShiftDate());
            endDate = asDateEndOfDay(shiftDTO.getShiftDate());
        }
        boolean absenceShiftExists = shiftMongoRepository.absenceShiftExistsByDate(shiftDTO.getUnitId(), startDate, endDate, shiftDTO.getStaffId());
        if (absenceShiftExists) {
            exceptionService.actionNotPermittedException("message.shift.overlap.with.full_day");
        }
    }

    public ShiftDTO escalationCorrectionInShift(ShiftDTO shiftDTO, ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO, boolean deleteShift,Date oldShiftStartDate,Date oldShiftEndDate,boolean escalated) {
            Shift shift = shiftMongoRepository.findOne(shiftDTO.getId());
            ActivityWrapper activityWrapper = activityMongoRepository.findActivityAndTimeTypeByActivityId(shift.getActivities().get(0).getActivityId());
            boolean workingTypeShift = WORKING_TYPE.toString().equals(activityWrapper.getTimeType());
            boolean wtaEscalationExists = isCollectionNotEmpty(shiftWithViolatedInfoDTO.getViolatedRules().getWorkTimeAgreements());
        List<Shift> overLappedShifts = shiftMongoRepository.findShiftBetweenDurationByStaffId(shift.getStaffId(), workingTypeShift ? shiftDTO.getActivities().get(0).getStartDate() : oldShiftStartDate, workingTypeShift ? shiftDTO.getActivities().get(0).getEndDate() : oldShiftEndDate);
            List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(overLappedShifts.stream().map(Shift::getId).collect(Collectors.toList()));
            Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap = shiftViolatedRules.stream().collect(Collectors.toMap(ShiftViolatedRules::getShiftId, Function.identity()));
            if (deleteShift) {
                removeOverLappedEscalation(shiftDTO, overLappedShifts, shiftViolatedRulesMap);

            } else {
                if (workingTypeShift && isCollectionEmpty(overLappedShifts) && !wtaEscalationExists) {
                    shiftDTO.getEscalationFreeShiftIds().add(shiftDTO.getId());
                    shiftViolatedRulesMap.get(shiftDTO.getId()).setEscalationResolved(true);
                } else {
                    overLappedShifts.forEach(overLappedShift -> {
                        if (!shiftViolatedRulesMap.get(overLappedShift.getId()).getEscalationReasons().contains(ShiftEscalationReason.WORK_TIME_AGREEMENT) && !escalated) {
                            shiftDTO.getEscalationFreeShiftIds().add(overLappedShift.getId());
                            shiftViolatedRulesMap.get(overLappedShift.getId()).setEscalationResolved(true);
                        }
                    });
                }
            }
            shiftViolatedRulesMongoRepository.saveAll(shiftViolatedRulesMap.values());
            return shiftDTO;
    }

    private void removeOverLappedEscalation(ShiftDTO shiftDTO, List<Shift> overLappedShifts, Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap) {
        overLappedShifts.forEach(overLappedShift -> {
            if (!shiftMongoRepository.shiftOverLapped(shiftDTO.getStaffId(), overLappedShift.getStartDate(), overLappedShift.getEndDate(), shiftDTO.getId())) {
                if (!shiftViolatedRulesMap.get(overLappedShift.getId()).getEscalationReasons().contains(ShiftEscalationReason.WORK_TIME_AGREEMENT)) {
                    shiftDTO.getEscalationFreeShiftIds().add(overLappedShift.getId());
                    shiftViolatedRulesMap.get(overLappedShift.getId()).setEscalationResolved(true);
                }
            }
        });
    }

    public Set<BigInteger> getEscalationFreeShifts(List<BigInteger> shiftIds){
        List<ShiftViolatedRules> shiftViolatedRules = shiftViolatedRulesMongoRepository.findAllViolatedRulesByShiftIds(shiftIds);
        List<ShiftViolatedRules> escalationFreeViolatedRules=shiftViolatedRules.stream().filter(k->k.isEscalationResolved()).collect(Collectors.toList());
        return escalationFreeViolatedRules.stream().map(k->k.getShiftId()).collect(Collectors.toSet());
    }


}