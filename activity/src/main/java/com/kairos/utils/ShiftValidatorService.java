package com.kairos.utils;

/**
 * @author pradeep
 * @date - 11/5/18
 */

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.enums.Day;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;

/**
 * @author pradeep
 * @date - 10/5/18
 */
@Component
public class ShiftValidatorService {


    private static ExceptionService exceptionService;

    @Autowired
    public void setExceptionService(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
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


    public static void throwException(String exception,Object... param){
        exceptionService.invalidRequestException(exception,param);
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
        return (Comparator<ShiftWithActivityDTO>) (ShiftWithActivityDTO s1, ShiftWithActivityDTO s2) -> s1.getStartDate().compareTo(s2.getStartDate());
    }

    public static boolean isValid(MinMaxSetting minMaxSetting, int limitValue, int calculatedValue) {
        return minMaxSetting.equals(MinMaxSetting.MINIMUM) ? limitValue <= calculatedValue : limitValue >= calculatedValue;
    }

    public static List<LocalDate> getSortedAndUniqueDates(List<ShiftWithActivityDTO> shifts, ShiftWithActivityDTO shift) {
        List<LocalDate> dates = new ArrayList<>(shifts.stream().map(s -> DateUtils.asLocalDate(s.getStartDate())).sorted().collect(Collectors.toSet()));
        return dates;
    }


    public static Set<DayOfWeek> getValidDays(List<DayTypeDTO> dayTypeDTOS, List<Long> dayTypeIds) {
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        Map<Long, DayTypeDTO> dayTypeDTOMap = dayTypeDTOS.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        List<Day> days = dayTypeIds.stream().flatMap(dayTypeId -> dayTypeDTOMap.get(dayTypeId).getValidDays().stream()).collect(Collectors.toList());
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
                    if (interval.contains(DateUtils.getZoneDateTime(shift.getStartDate()).get(ChronoField.MINUTE_OF_DAY))) {
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
        ZonedDateTime endDate = null;
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
                interval = new DateTimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).minusDays((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.getZoneDateTime(shift.getEndDate()).plusDays((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
            case WEEKS:
                interval = new DateTimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).minusWeeks((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.getZoneDateTime(shift.getEndDate()).plusWeeks((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
            case MONTHS:
                interval = new DateTimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).minusMonths((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.getZoneDateTime(shift.getEndDate()).plusMonths((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
            case YEARS:
                interval = new DateTimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).minusYears((int) intervalValue).truncatedTo(ChronoUnit.DAYS), DateUtils.getZoneDateTime(shift.getEndDate()).plusYears((int) intervalValue).truncatedTo(ChronoUnit.DAYS));
                break;
        }
        return interval;
    }

    public static List<ShiftWithActivityDTO> getShiftsByInterval(DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, TimeInterval timeInterval) {
        List<ShiftWithActivityDTO> updatedShifts = new ArrayList<>();
        shifts.forEach(s -> {
            if ((dateTimeInterval.contains(s.getStartDate()) || dateTimeInterval.contains(s.getEndDate())) && (timeInterval == null || timeInterval.contains(DateUtils.getZoneDateTime(s.getStartDate()).get(ChronoField.MINUTE_OF_DAY)))) {
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
            if (dateTimeInterval.contains(shift.getStartDate())) {
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
        }
        if (infoWrapper.getUser().getManagement() && phaseTemplateValue.isManagementCanIgnore()) {
            counterValue = ruleTemplate.getManagementCanIgnoreCounter();
        }
        return counterValue != null ? counterValue - infoWrapper.getCounterMap().getOrDefault(ruleTemplate.getId(), 0) : null;

    }

    public static List<ShiftWithActivityDTO> filterShifts(List<ShiftWithActivityDTO> shifts, List<BigInteger> timeTypeIds, List<BigInteger> plannedTimeIds, List<BigInteger> activitieIds) {
        List<ShiftWithActivityDTO> shiftQueryResultWithActivities = new ArrayList<>();
        if (timeTypeIds != null && !timeTypeIds.isEmpty()) {
            shifts.forEach(s -> {
                for (ShiftActivity shiftActivity : s.getActivities()) {
                    if ((timeTypeIds == null || timeTypeIds.contains(shiftActivity.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) && (plannedTimeIds == null || plannedTimeIds.contains(s.getPlannedTypeId())) && (activitieIds == null || activitieIds.contains(shiftActivity.getActivity().getId())))) {
                        shiftQueryResultWithActivities.add(s);
                    }
                }
            });
        }
        return shiftQueryResultWithActivities;
    }


    public static DateTimeInterval getIntervalByRuleTemplates(ShiftWithActivityDTO shift, List<WTABaseRuleTemplate> wtaBaseRuleTemplates) {
        DateTimeInterval interval = new DateTimeInterval(shift.getStartDate().getTime(), shift.getEndDate().getTime());
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
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift, seniorDaysPerYearWTATemplate.getNumberOfWeeks().intValue(), seniorDaysPerYearWTATemplate.getValidationStartDate()));

                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift, childCareDaysCheckWTATemplate.getNumberOfWeeks(), childCareDaysCheckWTATemplate.getValidationStartDate()));
                    break;
            }
        }
        return interval;
    }


    public static boolean isValidForDay(List<Long> dayTypeIds, RuleTemplateSpecificInfo infoWrapper) {
        DayOfWeek shiftDay = DateUtils.asLocalDate(infoWrapper.getShift().getStartDate()).getDayOfWeek();
        return getValidDays(infoWrapper.getDayTypes(), dayTypeIds).stream().filter(day -> day.equals(shiftDay)).findAny().isPresent();
    }


}