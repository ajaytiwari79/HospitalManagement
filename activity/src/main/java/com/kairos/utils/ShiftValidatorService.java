package com.kairos.utils;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.dto.activity.shift.*;
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
import com.kairos.persistence.model.activity.tabs.CompositeActivity;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.model.time_bank.DailyTimeBankEntry;
import com.kairos.persistence.model.unit_settings.TimeAttendanceGracePeriod;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staff_settings.StaffActivitySettingRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
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

    private static ExceptionService exceptionService;

    @Autowired
    public void setExceptionService(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }


    public void validateGracePeriod(ShiftDTO shiftDTO, Boolean validatedByStaff, Long unitId) {
        DateTimeInterval graceInterval;
        TimeAttendanceGracePeriod timeAttendanceGracePeriod = timeAttendanceGracePeriodRepository.findByUnitId(unitId);
        if (validatedByStaff) {
            graceInterval = getGracePeriodInterval(timeAttendanceGracePeriod, shiftDTO.getActivities().get(0).getStartDate(), validatedByStaff);
        } else {
            if (shiftDTO.getValidatedByStaffDate() == null) {
                exceptionService.invalidRequestException("message.shift.cannot.validated");
            }
            graceInterval = getGracePeriodInterval(timeAttendanceGracePeriod, DateUtils.asDate(shiftDTO.getValidatedByStaffDate()), validatedByStaff);
        }
        if (!graceInterval.contains(shiftDTO.getActivities().get(0).getStartDate())) {
            exceptionService.invalidRequestException("message.shift.cannot.update");
        }
    }

    public DateTimeInterval getGracePeriodInterval(TimeAttendanceGracePeriod timeAttendanceGracePeriod, Date date, boolean forStaff) {

        ZonedDateTime startDate = DateUtils.asZoneDateTime(date).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = DateUtils.asZoneDateTime(date).plusDays(1).truncatedTo(ChronoUnit.DAYS);
        if (forStaff) {
            startDate = startDate.minusDays(timeAttendanceGracePeriod.getStaffGracePeriodDays());
        } else {
            startDate = startDate.minusDays(timeAttendanceGracePeriod.getManagementGracePeriodDays());
        }
        return new DateTimeInterval(startDate, endDate);
    }


    public ShiftWithViolatedInfoDTO validateShiftWithActivity(Phase phase, WTAQueryResultDTO wtaQueryResultDTO, ShiftWithActivityDTO shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO,boolean byTandAPhase) {
        if (!Optional.ofNullable(wtaQueryResultDTO).isPresent()) {
            exceptionService.actionNotPermittedException("message.wta.notFound");
        }
        if (wtaQueryResultDTO.getEndDate() != null && new DateTime(wtaQueryResultDTO.getEndDate()).isBefore(shift.getActivitiesEndDate().getTime())) {
            exceptionService.actionNotPermittedException("message.wta.expired-unit");
        }
        RuleTemplateSpecificInfo ruleTemplateSpecificInfo = getRuleTemplateSpecificInfo(phase, shift, wtaQueryResultDTO, staffAdditionalInfoDTO);
        //TODO It should work on Multiple activity
        List<Long> dayTypeIds = shift.getActivities().get(0).getActivity().getRulesActivityTab().getDayTypes();
        Specification<ShiftWithActivityDTO> activitySkillSpec = new StaffAndSkillSpecification(staffAdditionalInfoDTO.getSkills());
        Specification<ShiftWithActivityDTO> activityEmploymentTypeSpecification = new EmploymentTypeSpecification(staffAdditionalInfoDTO.getUnitPosition().getEmploymentType());
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffAdditionalInfoDTO.getUnitPosition().getExpertise());
        Specification<ShiftWithActivityDTO> wtaRulesSpecification = new WTARulesSpecification(ruleTemplateSpecificInfo, wtaQueryResultDTO.getRuleTemplates());
        //TODO It should work on Multiple activity
        Specification<ShiftWithActivityDTO> staffEmploymentSpecification = new StaffEmploymentSpecification(phase, shift.getActivities().get(0).getActivity(), staffAdditionalInfoDTO);

        Specification<ShiftWithActivityDTO> activitySpecification = activityEmploymentTypeSpecification
                .and(activityExpertiseSpecification)
                .and(activitySkillSpec)
                .and(wtaRulesSpecification)
                .and(staffEmploymentSpecification);
        ;
        if(!byTandAPhase){
            Specification<ShiftWithActivityDTO> shiftTimeLessThan = new ShiftStartTimeLessThan(staffAdditionalInfoDTO.getUnitTimeZone(), shift.getActivitiesStartDate(), shift.getActivities().get(0).getActivity().getRulesActivityTab().getPlannedTimeInAdvance());
            activitySpecification.and(shiftTimeLessThan);
        }
        if (dayTypeIds != null) {
            Set<DayOfWeek> validDays = getValidDays(staffAdditionalInfoDTO.getDayTypes(), dayTypeIds);
            Specification<ShiftWithActivityDTO> activityDayTypeSpec = new DayTypeSpecification(validDays, shift.getActivitiesStartDate());
            activitySpecification.and(activityDayTypeSpec);
        }
        activitySpecification.validateRules(shift);
        ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = new ShiftWithViolatedInfoDTO(ruleTemplateSpecificInfo.getViolatedRules());
        return shiftWithViolatedInfoDTO;
    }


    private RuleTemplateSpecificInfo getRuleTemplateSpecificInfo(Phase phase, ShiftWithActivityDTO shift, WTAQueryResultDTO wtaQueryResultDTO, StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        logger.info("Current phase is " + phase.getName() + " for date " + new DateTime(shift.getActivities().get(0).getStartDate()));
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(shift.getUnitId(), DateUtils.asLocalDate(shift.getActivities().get(0).getStartDate()));
        if (planningPeriod == null) {
            exceptionService.actionNotPermittedException("message.shift.planning.period.exit", shift.getActivities().get(0).getStartDate());
        }
        List<StaffWTACounter> staffWTACounters = wtaCounterRepository.getStaffWTACounterByDate(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.asDate(planningPeriod.getStartDate()), DateUtils.asDate(planningPeriod.getEndDate()), staffAdditionalInfoDTO.getUserAccessRoleDTO().getStaff());
        DateTimeInterval intervalByRuleTemplates = getIntervalByRuleTemplates(shift, wtaQueryResultDTO.getRuleTemplates());
        List<ShiftWithActivityDTO> shifts = shiftMongoRepository.findAllShiftsBetweenDurationByUEP(staffAdditionalInfoDTO.getUnitPosition().getId(), DateUtils.asDate(intervalByRuleTemplates.getStart()), DateUtils.asDate(intervalByRuleTemplates.getEnd()));
        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByUnitPositionAndBeforeDate(staffAdditionalInfoDTO.getUnitPosition().getId(), shift.getActivities().get(0).getStartDate());
        Map<BigInteger, Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(StaffWTACounter::getRuleTemplateId, sc -> sc.getCount()));
        Date endTimeOfInterval = DateUtils.getDateByZoneDateTime(ZonedDateTime.ofInstant(shift.getActivities().get(0).getEndDate().toInstant(), ZoneId.systemDefault()).plusDays(1).truncatedTo(ChronoUnit.DAYS));
        Interval interval = new Interval(DateUtils.getLongFromLocalDate(staffAdditionalInfoDTO.getUnitPosition().getStartDate()),
                staffAdditionalInfoDTO.getUnitPosition().getEndDate() == null ? endTimeOfInterval.getTime() : DateUtils.getLongFromLocalDate(staffAdditionalInfoDTO.getUnitPosition().getEndDate()));
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = new UnitPositionWithCtaDetailsDTO(staffAdditionalInfoDTO.getUnitPosition().getId(), staffAdditionalInfoDTO.getUnitPosition().getTotalWeeklyMinutes(), staffAdditionalInfoDTO.getUnitPosition().getWorkingDaysInWeek(),
                staffAdditionalInfoDTO.getUnitPosition().getStartDate(), staffAdditionalInfoDTO.getUnitPosition().getEndDate() != null ? staffAdditionalInfoDTO.getUnitPosition().getEndDate() : null);
        int totalTimeBank = -timeBankCalculationService.calculateTimeBankForInterval(interval, unitPositionWithCtaDetailsDTO, false, dailyTimeBankEntries, false);
        return new RuleTemplateSpecificInfo(shifts, shift, staffAdditionalInfoDTO.getTimeSlotSets(), phase.getName(), new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(), DateUtils.asDate(planningPeriod.getEndDate()).getTime()), staffWTACounterMap, staffAdditionalInfoDTO.getDayTypes(), staffAdditionalInfoDTO.getUser(), totalTimeBank);
    }

    public List<String> validateShiftWhileCopy(ShiftWithActivityDTO shiftWithActivityDTO, StaffUnitPositionDetails staffUnitPositionDetails) {
        Specification<ShiftWithActivityDTO> activityEmploymentTypeSpecification = new EmploymentTypeSpecification(staffUnitPositionDetails.getEmploymentType());
        Specification<ShiftWithActivityDTO> activityExpertiseSpecification = new ExpertiseSpecification(staffUnitPositionDetails.getExpertise());

        Specification<ShiftWithActivityDTO> activitySpecification = activityEmploymentTypeSpecification.and(activityExpertiseSpecification);
        return activitySpecification.isSatisfiedString(shiftWithActivityDTO);
    }



    public void validateStatusOfShiftOnUpdate(Shift shift,ShiftDTO shiftDTO){
        for (ShiftActivity updateShiftActivity : shiftDTO.getActivities()) {
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                boolean notValid = (shiftActivity.getStatus().contains(ShiftStatus.FIXED) || shiftActivity.getStatus().contains(ShiftStatus.PUBLISHED) || shiftActivity.getStatus().contains(ShiftStatus.LOCKED)) && (updateShiftActivity==null || updateShiftActivity.getStartDate().equals(shift.getStartDate()) || updateShiftActivity.getEndDate().equals(shift.getEndDate()));
                if (notValid) {
                    exceptionService.actionNotPermittedException("message.shift.state.update", shiftActivity.getStatus());
                }
            }
        }

    }

   public void verifyShiftActivities(Set<AccessGroupRole> roles,Long employmentTypeId, Map<BigInteger, com.kairos.dto.activity.activity.activity_tabs.PhaseTemplateValue> phaseTemplateValue, ShiftActivityIdsDTO shiftActivityIdsDTO){
        boolean staff=roles.contains(AccessGroupRole.STAFF);
        boolean management=roles.contains(AccessGroupRole.MANAGEMENT);
        phaseTemplateValue.forEach((k,v)->{
            if(shiftActivityIdsDTO.getActivitiesToAdd().contains(k)){
                if((!v.getEligibleEmploymentTypes().contains(employmentTypeId)) || management && !v.isEligibleForManagement() ){
                    exceptionService.actionNotPermittedException("error.shift.not.authorised.phase");
                }
            }
            if(shiftActivityIdsDTO.getActivitiesToEdit().contains(k)){
                if(!CollectionUtils.containsAny(v.getAllowedSettings().getCanEdit(),roles)){
                    exceptionService.actionNotPermittedException("error.shift.not.editable.phase");
                }
            }
            if(shiftActivityIdsDTO.getActivitiesToDelete().contains(k)){
                if((management && !v.isManagementCanDelete()) || (staff && !v.isStaffCanDelete())){
                    exceptionService.actionNotPermittedException("error.shift.not.deletable.phase");
                }
            }

        });
   }

    public void validateStatusOfShiftOnDelete(Shift shift){
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


    public static Set<DayOfWeek> getValidDays(List<DayTypeDTO> dayTypeDTOS, List<Long> dayTypeIds) {
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        Map<Long, DayTypeDTO> dayTypeDTOMap = dayTypeDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<Day> days = dayTypeIds.stream().filter(s->dayTypeDTOMap.containsKey(s)).flatMap(dayTypeId -> dayTypeDTOMap.get(dayTypeId).getValidDays().stream()).collect(Collectors.toList());
        days.forEach(day -> {
            if (!day.equals(Day.EVERYDAY)) {
                dayOfWeeks.add(DayOfWeek.valueOf(day.name()));
            } else if (day.equals(Day.EVERYDAY)) {
                dayOfWeeks.addAll(Arrays.asList(DayOfWeek.values()));
            }
        });

        return new HashSet<>(dayOfWeeks);
    }


    public static TimeInterval getTimeSlotByPartOfDay(List<PartOfDay> partOfDays, List<TimeSlotWrapper> timeSlotWrappers, ShiftWithActivityDTO shift) {
        TimeInterval timeInterval = null;
        for (PartOfDay partOfDay : partOfDays) {
            for (TimeSlotWrapper timeSlotWrapper : timeSlotWrappers) {
                if (partOfDay.getValue().equals(timeSlotWrapper.getName())) {
                    TimeInterval interval = new TimeInterval(((timeSlotWrapper.getStartHour() * 60) + timeSlotWrapper.getStartMinute()), ((timeSlotWrapper.getEndHour() * 60) + timeSlotWrapper.getEndMinute()));
                    if (interval.contains(DateUtils.asZoneDateTime(shift.getActivities().get(0).getStartDate()).get(ChronoField.MINUTE_OF_DAY))) {
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
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getActivities().get(0).getStartDate()).minusDays((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.asZoneDateTime(shift.getActivities().get(0).getEndDate()).plusDays((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
            case WEEKS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getActivities().get(0).getStartDate()).minusWeeks((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.asZoneDateTime(shift.getActivities().get(0).getEndDate()).plusWeeks((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
            case MONTHS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getActivities().get(0).getStartDate()).minusMonths((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.asZoneDateTime(shift.getActivities().get(0).getEndDate()).plusMonths((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
            case YEARS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getActivities().get(0).getStartDate()).minusYears((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.asZoneDateTime(shift.getActivities().get(0).getEndDate()).plusYears((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
        }
        return interval;
    }

    public static List<ShiftWithActivityDTO> getShiftsByInterval(DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, TimeInterval timeInterval) {
        List<ShiftWithActivityDTO> updatedShifts = new ArrayList<>();
        shifts.forEach(s -> {
            if ((dateTimeInterval.contains(s.getActivitiesStartDate()) || dateTimeInterval.contains(s.getActivitiesEndDate())) && (timeInterval == null || timeInterval.contains(DateUtils.asZoneDateTime(s.getActivitiesStartDate()).get(ChronoField.MINUTE_OF_DAY)))) {
                updatedShifts.add(s);
            }
        });
        return updatedShifts;
    }

    public static DateTimeInterval getIntervalByNumberOfWeeks(ShiftWithActivityDTO shift, int numberOfWeeks, LocalDate validationStartDate) {
        if (numberOfWeeks == 0 || validationStartDate == null) {
            throwException("message.ruleTemplate.weeks.notNull");
        }
        DateTimeInterval dateTimeInterval;
        LocalDate endDate = validationStartDate.plusWeeks(numberOfWeeks);
        while (true) {
            dateTimeInterval = new DateTimeInterval(validationStartDate.atStartOfDay(ZoneId.systemDefault()), endDate.atStartOfDay(ZoneId.systemDefault()));
            endDate = validationStartDate.plusWeeks(numberOfWeeks);
            if (dateTimeInterval.contains(shift.getActivitiesStartDate())) {
                break;
            }
            validationStartDate = endDate;
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
        } else if (infoWrapper.getUser().getManagement() && phaseTemplateValue.isManagementCanIgnore()) {
            counterValue = ruleTemplate.getManagementCanIgnoreCounter();
        }
        return counterValue != null ? infoWrapper.getCounterMap().getOrDefault(ruleTemplate.getId(), counterValue) : null;

    }

    public static List<ShiftWithActivityDTO> filterShifts(List<ShiftWithActivityDTO> shifts, List<BigInteger> timeTypeIds, List<BigInteger> plannedTimeIds, List<BigInteger> activitieIds) {
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = new ArrayList<>();
        if (timeTypeIds != null && !timeTypeIds.isEmpty()) {
            shifts.forEach(s -> {
                for (ShiftActivityDTO shiftActivity : s.getActivities()) {
                    if ((timeTypeIds == null || timeTypeIds.contains(shiftActivity.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) && (plannedTimeIds == null || plannedTimeIds.contains(shiftActivity.getPlannedTimeId())) && (activitieIds == null || activitieIds.contains(shiftActivity.getActivity().getId())))) {
                        shiftQueryResultWithActivities.add(s);
                    }
                }
            });
        }
        return shiftQueryResultWithActivities;
    }


    public static DateTimeInterval getIntervalByRuleTemplates(ShiftWithActivityDTO shift, List<WTABaseRuleTemplate> wtaBaseRuleTemplates) {
        DateTimeInterval interval = new DateTimeInterval(shift.getActivities().get(0).getStartDate().getTime(), shift.getActivities().get(0).getEndDate().getTime());
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
                case VETO_PER_PERIOD:
                    VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = (VetoPerPeriodWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift, vetoPerPeriodWTATemplate.getNumberOfWeeks(), vetoPerPeriodWTATemplate.getValidationStartDate()));

                    break;
                case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                    NumberOfWeekendShiftsInPeriodWTATemplate numberOfWeekendShiftsInPeriodWTATemplate = (NumberOfWeekendShiftsInPeriodWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, numberOfWeekendShiftsInPeriodWTATemplate.getIntervalUnit(), numberOfWeekendShiftsInPeriodWTATemplate.getIntervalLength()));

                    break;
                case WEEKLY_REST_PERIOD:
                    RestPeriodInAnIntervalWTATemplate restPeriodInAnIntervalWTATemplate = (RestPeriodInAnIntervalWTATemplate) ruleTemplate;
                    DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(shift, restPeriodInAnIntervalWTATemplate.getIntervalUnit(), restPeriodInAnIntervalWTATemplate.getIntervalLength());
                    dateTimeInterval.setStart(dateTimeInterval.getStart().minusDays(1));
                    dateTimeInterval.setEnd(dateTimeInterval.getEnd().plusDays(1));
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
                /*case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift, seniorDaysPerYearWTATemplate.getNumberOfWeeks().intValue(), seniorDaysPerYearWTATemplate.getValidationStartDate()));

                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift, childCareDaysCheckWTATemplate.getNumberOfWeeks(), childCareDaysCheckWTATemplate.getValidationStartDate()));
                    break;*/
            }
        }
        return interval;
    }


    public static boolean isValidForDay(List<Long> dayTypeIds, RuleTemplateSpecificInfo infoWrapper) {
        DayOfWeek shiftDay = DateUtils.asLocalDate(infoWrapper.getShift().getActivitiesStartDate()).getDayOfWeek();
        return getValidDays(infoWrapper.getDayTypes(), dayTypeIds).stream().filter(day -> day.equals(shiftDay)).findAny().isPresent();
    }

    /**
     * @param shiftTimeDetailsMap
     * @param activityWrapperMap
     * @Auther Pavan
     */
    public void validateActivityTiming(Map<BigInteger, StaffActivitySetting> staffActivitySettingMap, Map<BigInteger, ShiftTimeDetails> shiftTimeDetailsMap, Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        shiftTimeDetailsMap.forEach((k, v) -> {
            Short shortestTime = staffActivitySettingMap.get(k) == null ? activityWrapperMap.get(k).getActivity().getRulesActivityTab().getShortestTime() : staffActivitySettingMap.get(k).getShortestTime();
            Short longestTime = staffActivitySettingMap.get(k) == null ? activityWrapperMap.get(k).getActivity().getRulesActivityTab().getLongestTime() : staffActivitySettingMap.get(k).getLongestTime();
            LocalTime earliestStartTime = staffActivitySettingMap.get(k) == null ? activityWrapperMap.get(k).getActivity().getRulesActivityTab().getEarliestStartTime() : staffActivitySettingMap.get(k).getEarliestStartTime();
            LocalTime latestStartTime = staffActivitySettingMap.get(k) == null ? activityWrapperMap.get(k).getActivity().getRulesActivityTab().getLatestStartTime() : staffActivitySettingMap.get(k).getLatestStartTime();
            if (shortestTime != null && v.getTotalTime() < shortestTime) {
                exceptionService.actionNotPermittedException("error.shift.duration.less_than.shortest_time");
            }
            if (longestTime != null && v.getTotalTime() > longestTime) {
                exceptionService.actionNotPermittedException("error.shift.duration_exceeds_longest_time");
            }
            if (earliestStartTime != null && earliestStartTime.isAfter(v.getActivityStartTime())) {
                exceptionService.actionNotPermittedException("error.start_time.greater_than.earliest_time");
            }
            if (latestStartTime != null && latestStartTime.isBefore(v.getActivityStartTime())) {
                exceptionService.actionNotPermittedException("error.start_time.less_than.latest_time");
            }
        });
    }


}