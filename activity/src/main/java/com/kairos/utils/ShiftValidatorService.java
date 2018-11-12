package com.kairos.utils;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffInterval;
import com.kairos.dto.activity.shift.*;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.Day;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.staffing_level.StaffingLevelActivityRank;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.model.unit_settings.TimeAttendanceGracePeriod;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelActivityRankRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.persistence.repository.unit_settings.TimeAttendanceGracePeriodRepository;
import com.kairos.persistence.repository.wta.StaffWTACounterRepository;
import com.kairos.rule_validator.Specification;
import com.kairos.rule_validator.activity.*;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;

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

    private static ExceptionService exceptionService;

    @Autowired
    public void setExceptionService(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }


    public void validateGracePeriod(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId, ShiftDTO staffShiftDTO) {
        DateTimeInterval graceInterval = null;
        TimeAttendanceGracePeriod timeAttendanceGracePeriod = timeAttendanceGracePeriodRepository.findByUnitId(unitId);
        if (validatedByStaff) {
            graceInterval = getGracePeriodInterval(timeAttendanceGracePeriod, shiftDTO.getActivities().get(0).getStartDate(), validatedByStaff);
        } else {
            if (staffShiftDTO.getValidated() == null) {
                exceptionService.invalidRequestException("message.shift.cannot.validated");
            }
            graceInterval = getGracePeriodInterval(timeAttendanceGracePeriod, DateUtils.asDate(staffShiftDTO.getValidated()), validatedByStaff);
        }
        if (!graceInterval.contains(shiftDTO.getActivities().get(0).getStartDate())) {
            exceptionService.invalidRequestException("message.shift.cannot.update");
        }
    }

    public DateTimeInterval getGracePeriodInterval(TimeAttendanceGracePeriod timeAttendanceGracePeriod, Date date, boolean forStaff) {
        ZonedDateTime startDate = DateUtils.asZoneDateTime(date).truncatedTo(ChronoUnit.DAYS).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        ZonedDateTime endDate = null;
        if (forStaff) {
            endDate = startDate.plusWeeks(1).plusDays(timeAttendanceGracePeriod.getStaffGracePeriodDays());
        } else {
            endDate = startDate.plusWeeks(1).plusDays(timeAttendanceGracePeriod.getManagementGracePeriodDays());
        }
        return new DateTimeInterval(startDate, endDate);
    }


    public ShiftWithViolatedInfoDTO validateShiftWithActivity(Phase phase, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Shift oldShift, Map<BigInteger, ActivityWrapper> activityWrapperMap, boolean byUpdate, boolean byTandAPhase) {
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException("message.wta.notFound");
        }
        if (wtaQueryResultDTO.getEndDate() != null && new DateTime(wtaQueryResultDTO.getEndDate()).isBefore(shift.getActivitiesEndDate().getTime())) {
            exceptionService.actionNotPermittedException("message.wta.expired-unit");
        }
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), DateUtils.asLocalDate(shift.getStartDate()));
        if (planningPeriod == null) {
            exceptionService.actionNotPermittedException("message.shift.planning.period.exits", shift.getStartDate());
        }
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(planningPeriod, phase, shift, wtaQueryResultDTO, staffAdditionalInfoDTO, activityWrapperMap);
        Specification<ShiftWithActivityDTO> activitySkillSpec = new StaffAndSkillSpecification(staffAdditionalInfoDTO.getSkills());
        Specification<ShiftWithActivityDTO> activityEmploymentTypeSpecification = new EmploymentTypeSpecification(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType());
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffAdditionalInfoDTO.getUnitPosition().getExpertise());
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTO.getRuleTemplates());
        Specification<ShiftWithActivityDTO> staffEmploymentSpecification = new StaffEmploymentSpecification(phase, staffAdditionalInfoDTO);
        Specification<ShiftWithActivityDTO> activitySpecification = activityEmploymentTypeSpecification
                .and(activityExpertiseSpecification)
                .and(activitySkillSpec)
                .and(wtaRulesSpecification)
                .and(staffEmploymentSpecification);
        if (byUpdate) {
            Specification<ShiftWithActivityDTO> activityPhaseSettingSpecification = new ActivityPhaseSettingSpecification(staffAdditionalInfoDTO, phase, activityWrapperMap.values(), oldShift);
            activitySpecification.and(activityPhaseSettingSpecification);
        }
        if (!byTandAPhase) {
            Specification<ShiftWithActivityDTO> shiftTimeLessThan = new ShiftStartTimeLessThan();
            activitySpecification.and(shiftTimeLessThan);
        }
        List<Long> dayTypeIds = shift.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getActivity().getRulesActivityTab().getDayTypes().stream()).collect(Collectors.toList());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(dayTypeIds)) {
            Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
            Set<DayOfWeek> validDays = getValidDays(dayTypeDTOMap, dayTypeIds);
            Specification<ShiftWithActivityDTO> activityDayTypeSpec = new DayTypeSpecification(validDays, shift.getActivitiesStartDate());
            activitySpecification.and(activityDayTypeSpec);
        }
        activitySpecification.validateRules(shift);
        List<ActivityRuleViolation> activityRuleViolations = validateTimingOfActivity(shift, new ArrayList<>(activityWrapperMap.keySet()), activityWrapperMap);
        ruleTemplateSpecificInfo.getViolatedRules().setActivities(activityRuleViolations);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO(ruleTemplateSpecificInfo.getViolatedRules());
        return shiftWithViolatedInfoDTO;
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
                errorMessages.add(exceptionService.convertMessage("error.shift.duration.less_than.shortest_time",shortestTime));
            }
            if (longestTime != null && shiftTimeDetails.getTotalTime() > longestTime) {
                errorMessages.add(exceptionService.convertMessage("error.shift.duration_exceeds_longest_time",longestTime));
            }
            if (earliestStartTime != null && earliestStartTime.isAfter(shiftTimeDetails.getActivityStartTime())) {
                errorMessages.add(exceptionService.convertMessage("error.start_time.greater_than.earliest_time",earliestStartTime));
            }
            if (latestStartTime != null && latestStartTime.isBefore(shiftTimeDetails.getActivityStartTime())) {
                errorMessages.add(exceptionService.convertMessage("error.start_time.less_than.latest_time",latestStartTime));
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
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()), staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff());
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shift, wtaQueryResultDTO.getRuleTemplates(),activityWrapperMap);
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUnitPosition(shift.getUnitPositionId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()));
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndBeforeDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shift.getStartDate());
        Map<String, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateName, sc -> sc.getCount()));
        Date endTimeOfInterval = DateUtils.getStartOfTheDay(DateUtils.asDate(DateUtils.asZoneDateTime(shift.getEndDate()).plusDays(1)));
        Interval interval = new Interval(DateUtils.getLongFromLocalDate(staffAdditionalInfoDTO.getUnitPosition().getStartDate()),
                staffAdditionalInfoDTO.getUnitPosition().getEndDate() == null ? endTimeOfInterval.getTime() : DateUtils.getLongFromLocalDate(staffAdditionalInfoDTO.getUnitPosition().getEndDate()));
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = new UnitPositionWithCtaDetailsDTO(staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes(), staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek(),
                staffAdditionalInfoDTO.getUnitPosition().getStartDate(), staffAdditionalInfoDTO.getUnitPosition().getEndDate() != null ? staffAdditionalInfoDTO.getUnitPosition().getEndDate() : null, staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyHours());
        List<DateTimeInterval> planningPeriodIntervals = timeBankCalculationService.getPlanningPeriodIntervals(shift.getUnitId(), interval.getStart().toDate(), interval.getEnd().toDate());
        int totalTimeBank = -timeBankCalculationService.calculateTimeBankForInterval(planningPeriodIntervals, interval, unitPositionWithCtaDetailsDTO, false, dailyTimeBankEntries, false);
        Map<String, TimeSlotWrapper> timeSlotWrapperMap = staffAdditionalInfoDTO.getTimeSlotSets().stream().collect(Collectors.toMap(TimeSlotWrapper::getName, v -> v));
        Map<Long, DayTypeDTO> dayTypeDTOMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        return new RuleTemplateSpecificInfo(new ArrayList<>(shifts), shift, timeSlotWrapperMap, phase.getName(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), staffWTACounterMap, dayTypeDTOMap, staffAdditionalInfoDTO.getUser(), totalTimeBank, activityWrapperMap,staffAdditionalInfoDTO.getStaffAge());
    }

    public List<String> validateShiftWhileCopy(ShiftWithActivityDTO shiftWithActivityDTO, StaffUnitPositionDetails staffUnitPositionDetails) {
        Specification<ShiftWithActivityDTO> activityEmploymentTypeSpecification = new EmploymentTypeSpecification(staffUnitPositionDetails.getEmploymentType());
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffUnitPositionDetails.getExpertise());

        Specification<ShiftWithActivityDTO> activitySpecification = activityEmploymentTypeSpecification.and(activityExpertiseSpecification);
        return activitySpecification.isSatisfiedString(shiftWithActivityDTO);
    }


    public void validateStatusOfShiftOnUpdate(Shift shift, ShiftDTO shiftDTO) {
        int i = 0;
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            boolean notValid = shiftActivity.getStatus().contains(ShiftStatus.FIXED) || shiftActivity.getStatus().contains(ShiftStatus.PUBLISHED) || shiftActivity.getStatus().contains(ShiftStatus.LOCKED);
            if (notValid) {
                try {
                    ShiftActivity updateShiftActivit = shiftDTO.getActivities().get(i);
                    if (updateShiftActivit == null || updateShiftActivit.getStartDate().equals(shift.getStartDate()) || updateShiftActivit.getEndDate().equals(shift.getEndDate())) {
                        exceptionService.actionNotPermittedException("message.shift.state.update", shiftActivity.getStatus());
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    exceptionService.actionNotPermittedException("message.shift.state.update", shiftActivity.getStatus());
                }

            }
            i++;
        }

    }


    public void validateStatusOfShiftOnDelete(Shift shift) {
        for (ShiftActivity shiftActivity : shift.getActivities()) {
            boolean notValid = shiftActivity.getStatus().contains(ShiftStatus.FIXED) || shiftActivity.getStatus().contains(ShiftStatus.PUBLISHED) || shiftActivity.getStatus().contains(ShiftStatus.LOCKED);
            if (notValid) {
                exceptionService.actionNotPermittedException("message.shift.state.update", shiftActivity.getStatus());
            }
        }

    }

    public static int getConsecutiveDaysInDate(List<LocalDate> localDates) {
        if (localDates.size() < 2) return 0;
        int count = 1;
        int max = 0;
        int l = 1;
        while (l < localDates.size()) {
            if (localDates.get(l - 1).equals(localDates.get(l).minusDays(1))) {
                count++;
            } else {
                count = 0;
            }
            if (count > max) {
                max = count;
            }
            l++;
        }
        return max;
    }


    public static void throwException(String exception, Object... param) {
        exceptionService.invalidRequestException(exception, param);
    }

    public static List<DateTimeInterval> getSortedIntervals(List<ShiftWithActivityDTO> shifts) {
        List<DateTimeInterval> intervals = new ArrayList<>();
        for (ShiftWithActivityDTO s : sortShifts(shifts)) {
            intervals.add(s.getDateTimeInterval());
        }
        return intervals;
    }

    public static List<ShiftWithActivityDTO> sortShifts(List<ShiftWithActivityDTO> shifts) {
        shifts.sort(getShiftStartTimeComparator());
        return shifts;
    }

    public static Comparator getShiftStartTimeComparator() {
        return (Comparator<ShiftWithActivityDTO>) (ShiftWithActivityDTO s1, ShiftWithActivityDTO s2) -> s1.getActivitiesStartDate().compareTo(s2.getActivitiesStartDate());
    }

    public static boolean isValid(MinMaxSetting minMaxSetting, int limitValue, int calculatedValue) {
        return minMaxSetting.equals(MinMaxSetting.MINIMUM) ? limitValue <= calculatedValue : limitValue >= calculatedValue;
    }

    public static List<LocalDate> getSortedAndUniqueDates(List<ShiftWithActivityDTO> shifts, ShiftWithActivityDTO shift) {
        List<LocalDate> dates = new ArrayList<>(shifts.stream().map(s -> DateUtils.asLocalDate(s.getActivitiesStartDate())).sorted().collect(Collectors.toSet()));
        return dates;
    }


    public static Set<DayOfWeek> getValidDays(Map<Long, DayTypeDTO> dayTypeMap, List<Long> dayTypeIds) {
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        List<Day> days = dayTypeIds.stream().filter(s -> dayTypeMap.containsKey(s)).flatMap(dayTypeId -> dayTypeMap.get(dayTypeId).getValidDays().stream()).collect(Collectors.toList());
        days.forEach(day -> {
            if (!day.equals(Day.EVERYDAY)) {
                dayOfWeeks.add(DayOfWeek.valueOf(day.name()));
            } else if (day.equals(Day.EVERYDAY)) {
                dayOfWeeks.addAll(Arrays.asList(DayOfWeek.values()));
            }
        });

        return new HashSet<>(dayOfWeeks);
    }


    public static TimeInterval getTimeSlotByPartOfDay(List<PartOfDay> partOfDays, Map<String, TimeSlotWrapper> timeSlotWrapperMap, ShiftWithActivityDTO shift) {
        TimeInterval timeInterval = null;
        for (PartOfDay partOfDay : partOfDays) {
            if (timeSlotWrapperMap.containsKey(partOfDay.getValue())) {
                TimeSlotWrapper timeSlotWrapper = timeSlotWrapperMap.get(partOfDay.getValue());
                if (partOfDay.getValue().equals(timeSlotWrapper.getName())) {
                    TimeInterval interval = new TimeInterval(((timeSlotWrapper.getStartHour() * 60) + timeSlotWrapper.getStartMinute()), ((timeSlotWrapper.getEndHour() * 60) + timeSlotWrapper.getEndMinute()));
                    if (interval.contains(DateUtils.asZoneDateTime(shift.getStartDate()).get(ChronoField.MINUTE_OF_DAY))) {
                        timeInterval = interval;
                        break;
                    }
                }
            }
        }
        return timeInterval;
    }

    public static List<DateTimeInterval> getDaysIntervals(DateTimeInterval dateTimeInterval) {
        List<DateTimeInterval> intervals = new ArrayList<>();
        ZonedDateTime endDate;
        ZonedDateTime startDate = dateTimeInterval.getStart();
        while (true) {
            if (startDate.isBefore(dateTimeInterval.getEnd())) {
                endDate = startDate.plusDays(1);
                intervals.add(new DateTimeInterval(startDate, endDate));
                startDate = endDate;
            } else {
                break;
            }
        }
        return intervals;
    }

    public static DateTimeInterval getIntervalByRuleTemplate(ShiftWithActivityDTO shift, String intervalUnit, long intervalValue) {
        DateTimeInterval interval = null;
        if (intervalValue == 0 || StringUtils.isEmpty(intervalUnit)) {
            throwException("message.ruleTemplate.interval.notNull");
        }
        switch (intervalUnit) {
            case DAYS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getStartDate()).minusDays((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.asZoneDateTime(shift.getEndDate()).plusDays((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
            case WEEKS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getStartDate()).minusWeeks((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.asZoneDateTime(shift.getEndDate()).plusWeeks((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
            case MONTHS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getStartDate()).minusMonths((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.asZoneDateTime(shift.getEndDate()).plusMonths((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
            case YEARS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getStartDate()).minusYears((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.asZoneDateTime(shift.getEndDate()).plusYears((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
        }
        return interval;
    }

    public static List<ShiftWithActivityDTO> getShiftsByInterval(DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, TimeInterval timeInterval) {
        List<ShiftWithActivityDTO> updatedShifts = new ArrayList<>();
        shifts.forEach(s -> {
            if ((dateTimeInterval.contains(s.getStartDate()) || dateTimeInterval.contains(s.getEndDate())) && (timeInterval == null || timeInterval.contains(DateUtils.asZoneDateTime(s.getStartDate()).get(ChronoField.MINUTE_OF_DAY)))) {
                updatedShifts.add(s);
            }
        });
        return updatedShifts;
    }

    public static void brokeRuleTemplate(RuleTemplateSpecificInfo infoWrapper,Integer counterCount,boolean isValid,WTABaseRuleTemplate wtaBaseRuleTemplate){
        if (!isValid) {
            WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation;
            if (counterCount != null) {
                int counterValue = counterCount - 1;
                boolean canBeIgnore = true;
                if (counterValue < 0) {
                    counterCount = 0;
                    canBeIgnore = false;
                }
                workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(wtaBaseRuleTemplate.getId(),wtaBaseRuleTemplate.getName(),counterCount,true,canBeIgnore);
            }else {
                workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(wtaBaseRuleTemplate.getId(),wtaBaseRuleTemplate.getName(),0,true,false);
            }
            infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
        }
    }

    public static List<ShiftWithActivityDTO> getShiftsByIntervalAndActivityIds(Activity activity, Date shiftStartDate, List<ShiftWithActivityDTO> shifts, List<BigInteger> activitieIds) {
        List<ShiftWithActivityDTO> updatedShifts = new ArrayList<>();
        LocalDate shiftStartLocalDate = DateUtils.asLocalDate(shiftStartDate);
        Optional<CutOffInterval> cutOffIntervalOptional = activity.getRulesActivityTab().getCutOffIntervals().stream().filter(interval -> (interval.getStartDate().isBefore(shiftStartLocalDate) && interval.getEndDate().isAfter(shiftStartLocalDate) || interval.getStartDate().isEqual(shiftStartLocalDate))).findAny();
        if (cutOffIntervalOptional.isPresent()) {
            CutOffInterval cutOffInterval = cutOffIntervalOptional.get();
            for (ShiftWithActivityDTO shift : shifts) {
                LocalDate shiftLocalDate = DateUtils.asLocalDate(shift.getStartDate());
                if (CollectionUtils.containsAny(shift.getActivitIds(), activitieIds) && (cutOffInterval.getStartDate().isBefore(shiftLocalDate) && cutOffInterval.getEndDate().isAfter(shiftLocalDate) || cutOffInterval.getStartDate().isEqual(shiftLocalDate))) {
                    updatedShifts.add(shift);
                }
            }
        }
        return updatedShifts;
    }

    public static DateTimeInterval getIntervalByNumberOfWeeks(ShiftWithActivityDTO shift, int numberOfWeeks, LocalDate validationStartDate) {
        if (numberOfWeeks == 0 || validationStartDate == null) {
            throwException("message.ruleTemplate.weeks.notNull");
        }
        DateTimeInterval dateTimeInterval;
        LocalDate endDate = validationStartDate.plusWeeks(numberOfWeeks);
        if(validationStartDate.minusDays(1).isBefore(DateUtils.asLocalDate(shift.getStartDate()))) {
            while (true) {
                dateTimeInterval = new DateTimeInterval(validationStartDate.atStartOfDay(ZoneId.systemDefault()), endDate.atStartOfDay(ZoneId.systemDefault()));
                if (dateTimeInterval.contains(shift.getStartDate())) {
                    break;
                }
                validationStartDate = endDate;
                endDate = validationStartDate.plusWeeks(numberOfWeeks);
            }
        }else{
            dateTimeInterval = new DateTimeInterval(shift.getStartDate(),shift.getEndDate());
        }
        return dateTimeInterval;
    }

    public static Integer[] getValueByPhase(RuleTemplateSpecificInfo infoWrapper, List<PhaseTemplateValue> phaseTemplateValues, WTABaseRuleTemplate ruleTemplate) {
        Integer[] limitAndCounter = new Integer[2];
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (infoWrapper.getPhase().equals(phaseTemplateValue.getPhaseName())) {
                limitAndCounter[0] = (int) (infoWrapper.getUser().getStaff() ? phaseTemplateValue.getStaffValue() : phaseTemplateValue.getManagementValue());
                limitAndCounter[1] = getCounterValue(infoWrapper, phaseTemplateValue, ruleTemplate);
                break;
            }
        }
        return limitAndCounter;
    }

    public static boolean isValidForPhase(String phase, List<PhaseTemplateValue> phaseTemplateValues) {
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (phase.equals(phaseTemplateValue.getPhaseName())) {
                return !phaseTemplateValue.isDisabled();
            }
        }
        return false;
    }

    public static Integer getCounterValue(RuleTemplateSpecificInfo infoWrapper, PhaseTemplateValue phaseTemplateValue, WTABaseRuleTemplate ruleTemplate) {
        Integer counterValue = null;
        if (infoWrapper.getUser().getStaff() && phaseTemplateValue.isStaffCanIgnore()) {
            counterValue = ruleTemplate.getStaffCanIgnoreCounter();
            if (counterValue == null) {
                throwException("message.ruleTemplate.counter.value.notNull", ruleTemplate.getName());
            }
        } else if (infoWrapper.getUser().getManagement() && phaseTemplateValue.isManagementCanIgnore()) {
            counterValue = ruleTemplate.getManagementCanIgnoreCounter();
            if (counterValue == null) {
                throwException("message.ruleTemplate.counter.value.notNull", ruleTemplate.getName());
            }
        }
        return counterValue != null ? infoWrapper.getCounterMap().getOrDefault(ruleTemplate.getName(), counterValue) : null;

    }

    public static List<ShiftWithActivityDTO> filterShiftsByActivityIds(List<ShiftWithActivityDTO> shifts, List<BigInteger> activitieIds) {
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = new ArrayList<>();
        shifts.forEach(shift -> {
            boolean isValidShift = (CollectionUtils.isNotEmpty(activitieIds) && CollectionUtils.containsAny(activitieIds, shift.getActivitIds()));
            if (isValidShift) {
                shiftQueryResultWithActivities.add(shift);
            }

        });
        return shiftQueryResultWithActivities;
    }

    public static List<ShiftWithActivityDTO> filterShiftsByTimeTypeIds(List<ShiftWithActivityDTO> shifts, List<BigInteger> timeTypeIds) {
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = new ArrayList<>();
        shifts.forEach(shift -> {
            boolean isValidShift = (CollectionUtils.isNotEmpty(timeTypeIds) && CollectionUtils.containsAny(timeTypeIds, shift.getActivitiesTimeTypeIds()));
            if (isValidShift) {
                shiftQueryResultWithActivities.add(shift);
            }

        });
        return shiftQueryResultWithActivities;
    }


    public static List<ShiftWithActivityDTO> filterShiftsByPlannedTypeAndTimeTypeIds(List<ShiftWithActivityDTO> shifts, List<BigInteger> timeTypeIds, List<BigInteger> plannedTimeIds) {
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = new ArrayList<>();
        shifts.forEach(shift -> {
            boolean isValidShift = (CollectionUtils.isNotEmpty(timeTypeIds) && CollectionUtils.containsAny(timeTypeIds, shift.getActivitiesTimeTypeIds())) && (CollectionUtils.isNotEmpty(plannedTimeIds) && CollectionUtils.containsAny(plannedTimeIds, shift.getActivitiesPlannedTimeIds()));
            if (isValidShift) {
                shiftQueryResultWithActivities.add(shift);
            }

        });
        return shiftQueryResultWithActivities;
    }

    public static DateTimeInterval getIntervalByRuleTemplates(ShiftWithActivityDTO shift, List<WTABaseRuleTemplate> wtaBaseRuleTemplates,Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        DateTimeInterval interval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
        for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case NUMBER_OF_PARTOFDAY:
                    NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = (NumberOfPartOfDayShiftsWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, numberOfPartOfDayShiftsWTATemplate.getIntervalUnit(), numberOfPartOfDayShiftsWTATemplate.getIntervalLength()));
                    break;
                case DAYS_OFF_IN_PERIOD:
                    DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = (DaysOffInPeriodWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, daysOffInPeriodWTATemplate.getIntervalUnit(), daysOffInPeriodWTATemplate.getIntervalLength()));

                    break;
                case AVERAGE_SHEDULED_TIME:
                    AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = (AverageScheduledTimeWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, averageScheduledTimeWTATemplate.getIntervalUnit(), averageScheduledTimeWTATemplate.getIntervalLength()));
                    break;
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift, vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate()));

                    break;
                case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                    NumberOfWeekendShiftsInPeriodWTATemplate numberOfWeekendShiftsInPeriodWTATemplate = (NumberOfWeekendShiftsInPeriodWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, numberOfWeekendShiftsInPeriodWTATemplate.getIntervalUnit(), numberOfWeekendShiftsInPeriodWTATemplate.getIntervalLength()));

                    break;
                case WEEKLY_REST_PERIOD:
                    RestPeriodInAnIntervalWTATemplate restPeriodInAnIntervalWTATemplate = (RestPeriodInAnIntervalWTATemplate) ruleTemplate;
                    DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(shift, restPeriodInAnIntervalWTATemplate.getIntervalUnit(), restPeriodInAnIntervalWTATemplate.getIntervalLength());
                    //dateTimeInterval.setStart(dateTimeInterval.getStart().minusDays(1));
                    //dateTimeInterval.setEnd(dateTimeInterval.getEnd().plusDays(1));
                    interval = interval.addInterval(dateTimeInterval);

                    break;
                case SHORTEST_AND_AVERAGE_DAILY_REST:
                    ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = (ShortestAndAverageDailyRestWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, shortestAndAverageDailyRestWTATemplate.getIntervalUnit(), shortestAndAverageDailyRestWTATemplate.getIntervalLength()));

                    break;
                case NUMBER_OF_SHIFTS_IN_INTERVAL:
                    ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate = (ShiftsInIntervalWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, shiftsInIntervalWTATemplate.getIntervalUnit(), shiftsInIntervalWTATemplate.getIntervalLength()));
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByActivity(activityWrapperMap,shift.getStartDate(),seniorDaysPerYearWTATemplate.getActivityIds()));
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByActivity(activityWrapperMap,shift.getStartDate(),childCareDaysCheckWTATemplate.getActivityIds()));
                    break;
            }
        }
        return interval;
    }


    public static  DateTimeInterval getIntervalByActivity(Map<BigInteger, ActivityWrapper> activityWrapperMap,Date shiftStartDate,List<BigInteger> activityIds){
        LocalDate shiftDate = DateUtils.asLocalDate(shiftStartDate);
        DateTimeInterval dateTimeInterval = new DateTimeInterval(shiftStartDate,DateUtils.asDate(DateUtils.asZoneDateTime(shiftStartDate).plusDays(1)));
        for (BigInteger activityId : activityIds) {
            if(activityWrapperMap.containsKey(activityId)){
                Activity activity = activityWrapperMap.get(activityId).getActivity();
                Optional<CutOffInterval> cutOffIntervalOptional = activity.getRulesActivityTab().getCutOffIntervals().stream().filter(cutOffInterval -> (cutOffInterval.getStartDate().isBefore(shiftDate) && cutOffInterval.getEndDate().isAfter(shiftDate) || cutOffInterval.getStartDate().isEqual(shiftDate))).findFirst();
                if(cutOffIntervalOptional.isPresent()){
                    CutOffInterval cutOffInterval = cutOffIntervalOptional.get();
                    dateTimeInterval.addInterval(new DateTimeInterval(DateUtils.asDate(cutOffInterval.getStartDate()),DateUtils.asDate(cutOffInterval.getEndDate())));
                }
            }
        }
        return dateTimeInterval;
    }


    public static boolean isValidForDay(List<Long> dayTypeIds, RuleTemplateSpecificInfo infoWrapper) {
        DayOfWeek shiftDay = DateUtils.asLocalDate(infoWrapper.getShift().getStartDate()).getDayOfWeek();
        return getValidDays(infoWrapper.getDayTypeMap(), dayTypeIds).stream().filter(day -> day.equals(shiftDay)).findAny().isPresent();
    }

    public void verifyRankAndStaffingLevel(List<ShiftActivity> shiftActivities, Long unitId, List<ActivityWrapper> activities, Phase phase, UserAccessRoleDTO userAccessRoleDTO) {
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
                    StaffingLevelActivityRank rankOfExisting = staffingLevelActivityRankRepository.findByStaffingLevelDateAndActivityId(DateUtils.asLocalDate(shiftActivities.get(0).getStartDate()), shiftActivities.get(0).getActivityId());
                    StaffingLevelActivityRank rankOfReplaced = staffingLevelActivityRankRepository.findByStaffingLevelDateAndActivityId(DateUtils.asLocalDate(shiftActivities.get(1).getStartDate()), shiftActivities.get(1).getActivityId());
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
                        exceptionService.actionNotPermittedException("message.staffingLevel.activity");
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
                if ((!v.getEligibleEmploymentTypes().contains(employmentTypeId)) || management && !v.isEligibleForManagement()) {
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

    public static List<ShiftWithActivityDTO> filterShiftsByIntervalAndVetoAndStopBricksActivity(DateTimeInterval interval,List<ShiftWithActivityDTO> shifts, List<BigInteger> activityIds) {
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = new ArrayList<>();
        shifts.forEach(shift -> {
            boolean isValidShift = (CollectionUtils.isNotEmpty(activityIds) && CollectionUtils.containsAny(shift.getActivitIds(), activityIds) && interval.overlaps(shift.getDateTimeInterval()));
            if (isValidShift) {
                shiftQueryResultWithActivities.add(shift);
            }

        });
        return shiftQueryResultWithActivities;
    }

    public static boolean validateVetoAndStopBrickRules(float totalBlockingPoints,int totalVeto,int totalStopBricks){
        return totalBlockingPoints>=totalVeto*VETO_BLOCKING_POINT+totalStopBricks*STOP_BRICK_BLOCKING_POINT;

    }
}