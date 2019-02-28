package com.kairos.utils.worktimeagreement;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffInterval;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.Day;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.wta.templates.template_types.*;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.constants.AppConstants.YEARS;
import static com.kairos.service.shift.ShiftValidatorService.throwException;
import static org.apache.commons.lang.StringUtils.isEmpty;

public class RuletemplateUtils {



    public static List<ShiftWithActivityDTO> getShiftsByIntervalAndActivityIds(Activity activity, Date shiftStartDate, List<ShiftWithActivityDTO> shifts, List<BigInteger> activitieIds) {
        List<ShiftWithActivityDTO> updatedShifts = new ArrayList<>();
        LocalDate shiftStartLocalDate = DateUtils.asLocalDate(shiftStartDate);
        Optional<CutOffInterval> cutOffIntervalOptional = activity.getRulesActivityTab().getCutOffIntervals().stream().filter(interval -> ((interval.getStartDate().isBefore(shiftStartLocalDate) || interval.getStartDate().isEqual(shiftStartLocalDate)) && (interval.getEndDate().isAfter(shiftStartLocalDate) || interval.getEndDate().isEqual(shiftStartLocalDate)))).findAny();
        if (cutOffIntervalOptional.isPresent()) {
            CutOffInterval cutOffInterval = cutOffIntervalOptional.get();
            for (ShiftWithActivityDTO shift : shifts) {
                DateTimeInterval interval = new DateTimeInterval(DateUtils.asDate(cutOffInterval.getStartDate()), DateUtils.asDate(cutOffInterval.getEndDate().plusDays(1)));
                if (CollectionUtils.containsAny(shift.getActivityIds(), activitieIds) && interval.contains(shift.getStartDate())) {
                    updatedShifts.add(shift);
                }
            }
        }
        return updatedShifts;
    }

    public static DateTimeInterval getIntervalByNumberOfWeeks(Date startDate, int numberOfWeeks, LocalDate validationStartDate, LocalDate planningPeriodEndDate) {
        if (numberOfWeeks == 0 || validationStartDate == null) {
            throwException("message.ruleTemplate.weeks.notNull");
        }
        DateTimeInterval dateTimeInterval = null;
        while (validationStartDate.isBefore(planningPeriodEndDate) || validationStartDate.equals(planningPeriodEndDate)) {
            dateTimeInterval = new DateTimeInterval(asDate(validationStartDate), asDate(validationStartDate.plusWeeks(numberOfWeeks)));
            if (dateTimeInterval.contains(startDate)) {
                break;
            }
            validationStartDate = validationStartDate.plusWeeks(numberOfWeeks);

        }
        return dateTimeInterval;
    }


    public static TimeInterval getTimeSlotByPartOfDay(List<PartOfDay> partOfDays, Map<String, TimeSlotWrapper> timeSlotWrapperMap, ShiftWithActivityDTO shift) {
        TimeInterval timeInterval = null;
        for (PartOfDay partOfDay : partOfDays) {
            if (timeSlotWrapperMap.containsKey(partOfDay.getValue())) {
                TimeSlotWrapper timeSlotWrapper = timeSlotWrapperMap.get(partOfDay.getValue());
                if (partOfDay.getValue().equals(timeSlotWrapper.getName())) {
                    int endMinutesOfInterval = (timeSlotWrapper.getEndHour() * 60) + timeSlotWrapper.getEndMinute();
                    int startMinutesOfInterval = (timeSlotWrapper.getStartHour() * 60) + timeSlotWrapper.getStartMinute();
                    TimeInterval interval = new TimeInterval(startMinutesOfInterval, endMinutesOfInterval);
                    int minuteOfTheDay = DateUtils.asZoneDateTime(shift.getStartDate()).get(ChronoField.MINUTE_OF_DAY);
                    if (minuteOfTheDay == (int) interval.getStartFrom() || interval.contains(minuteOfTheDay)) {
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

    public static void brokeRuleTemplate(RuleTemplateSpecificInfo infoWrapper, Integer counterCount, boolean isValid, WTABaseRuleTemplate wtaBaseRuleTemplate) {
        if (!isValid) {
            WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation;
            if (counterCount != null) {
                int counterValue = counterCount - 1;
                boolean canBeIgnore = true;
                if (counterValue < 0) {
                    counterCount = 0;
                    canBeIgnore = false;
                }
                workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(wtaBaseRuleTemplate.getId(), wtaBaseRuleTemplate.getName(), counterCount, true, canBeIgnore);
            } else {
                workTimeAgreementRuleViolation = new WorkTimeAgreementRuleViolation(wtaBaseRuleTemplate.getId(), wtaBaseRuleTemplate.getName(), 0, true, false);
            }
            infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
        }
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

    public static List<LocalDate> getSortedAndUniqueDates(List<ShiftWithActivityDTO> shifts) {
        List<LocalDate> dates = new ArrayList<>(shifts.stream().map(s -> DateUtils.asLocalDate(s.getStartDate())).collect(Collectors.toSet()));
        dates.sort((date1, date2) -> date1.compareTo(date2));
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

    public static void validateRuleTemplate(int numberOfWeeks, LocalDate validationStartDate) {
        if (numberOfWeeks == 0 || validationStartDate == null) {
            throwException("message.ruleTemplate.weeks.notNull");
        }
    }

    public static void validateRuleTemplate(long intervalLength, String intervalUnit) {
        if (intervalLength == 0 || isEmpty(intervalUnit)) {
            throwException("message.ruleTemplate.interval.notNull");
        }
    }

    public static DateTimeInterval getIntervalByRuleTemplates(ShiftWithActivityDTO shift, List<WTABaseRuleTemplate> wtaBaseRuleTemplates, Map<BigInteger, ActivityWrapper> activityWrapperMap, LocalDate planningPeriodEndDate) {
        DateTimeInterval interval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
        for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case NUMBER_OF_PARTOFDAY:
                    NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = (NumberOfPartOfDayShiftsWTATemplate) ruleTemplate;
                    validateRuleTemplate(numberOfPartOfDayShiftsWTATemplate.getIntervalLength(), numberOfPartOfDayShiftsWTATemplate.getIntervalUnit());
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, numberOfPartOfDayShiftsWTATemplate.getIntervalUnit(), numberOfPartOfDayShiftsWTATemplate.getIntervalLength()));
                    break;
                case DAYS_OFF_IN_PERIOD:
                    DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = (DaysOffInPeriodWTATemplate) ruleTemplate;
                    validateRuleTemplate(daysOffInPeriodWTATemplate.getIntervalLength(), daysOffInPeriodWTATemplate.getIntervalUnit());
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, daysOffInPeriodWTATemplate.getIntervalUnit(), daysOffInPeriodWTATemplate.getIntervalLength()));

                    break;
                case AVERAGE_SHEDULED_TIME:
                    AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = (AverageScheduledTimeWTATemplate) ruleTemplate;
                    validateRuleTemplate(averageScheduledTimeWTATemplate.getIntervalLength(), averageScheduledTimeWTATemplate.getIntervalUnit());
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, averageScheduledTimeWTATemplate.getIntervalUnit(), averageScheduledTimeWTATemplate.getIntervalLength()));
                    break;
                case VETO_AND_STOP_BRICKS:
                    VetoAndStopBricksWTATemplate vetoAndStopBricksWTATemplate = (VetoAndStopBricksWTATemplate) ruleTemplate;
                    validateRuleTemplate(vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate());
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift.getStartDate(), vetoAndStopBricksWTATemplate.getNumberOfWeeks(), vetoAndStopBricksWTATemplate.getValidationStartDate(),planningPeriodEndDate));

                    break;
                case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                    NumberOfWeekendShiftsInPeriodWTATemplate numberOfWeekendShiftsInPeriodWTATemplate = (NumberOfWeekendShiftsInPeriodWTATemplate) ruleTemplate;
                    validateRuleTemplate(numberOfWeekendShiftsInPeriodWTATemplate.getIntervalLength(), numberOfWeekendShiftsInPeriodWTATemplate.getIntervalUnit());
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, numberOfWeekendShiftsInPeriodWTATemplate.getIntervalUnit(), numberOfWeekendShiftsInPeriodWTATemplate.getIntervalLength()));

                    break;
                case WEEKLY_REST_PERIOD:
                    RestPeriodInAnIntervalWTATemplate restPeriodInAnIntervalWTATemplate = (RestPeriodInAnIntervalWTATemplate) ruleTemplate;
                    validateRuleTemplate(restPeriodInAnIntervalWTATemplate.getIntervalLength(), restPeriodInAnIntervalWTATemplate.getIntervalUnit());
                    DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(shift, restPeriodInAnIntervalWTATemplate.getIntervalUnit(), restPeriodInAnIntervalWTATemplate.getIntervalLength());
                    interval = interval.addInterval(dateTimeInterval);

                    break;
                case SHORTEST_AND_AVERAGE_DAILY_REST:
                    ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = (ShortestAndAverageDailyRestWTATemplate) ruleTemplate;
                    validateRuleTemplate(shortestAndAverageDailyRestWTATemplate.getIntervalLength(), shortestAndAverageDailyRestWTATemplate.getIntervalUnit());
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, shortestAndAverageDailyRestWTATemplate.getIntervalUnit(), shortestAndAverageDailyRestWTATemplate.getIntervalLength()));

                    break;
                case NUMBER_OF_SHIFTS_IN_INTERVAL:
                    ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate = (ShiftsInIntervalWTATemplate) ruleTemplate;
                    validateRuleTemplate(shiftsInIntervalWTATemplate.getIntervalLength(), shiftsInIntervalWTATemplate.getIntervalUnit());
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, shiftsInIntervalWTATemplate.getIntervalUnit(), shiftsInIntervalWTATemplate.getIntervalLength()));
                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByActivity(activityWrapperMap, shift.getStartDate(), seniorDaysPerYearWTATemplate.getActivityIds()));
                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) ruleTemplate;
                    interval = interval.addInterval(getIntervalByActivity(activityWrapperMap, shift.getStartDate(), childCareDaysCheckWTATemplate.getActivityIds()));
                    break;
                case WTA_FOR_CARE_DAYS:
                    WTAForCareDays wtaForCareDays = (WTAForCareDays) ruleTemplate;
                    interval = interval.addInterval(getIntervalByWTACareDaysRuleTemplate(shift, wtaForCareDays));
                    break;
                case CONSECUTIVE_WORKING_PARTOFDAY:
                    ConsecutiveWorkWTATemplate consecutiveWorkWTATemplate = (ConsecutiveWorkWTATemplate) ruleTemplate;
                    validateRuleTemplate(consecutiveWorkWTATemplate.getIntervalLength(), consecutiveWorkWTATemplate.getIntervalUnit());
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift, consecutiveWorkWTATemplate.getIntervalUnit(), consecutiveWorkWTATemplate.getIntervalLength()));
                    break;

            }
        }
        return interval;
    }

    public static DateTimeInterval getIntervalByWTACareDaysRuleTemplate(ShiftWithActivityDTO shift, WTAForCareDays wtaForCareDays) {
        LocalDate shiftDate = DateUtils.asLocalDate(shift.getStartDate());
        Map<BigInteger, ActivityCareDayCount> careDayCountMap = wtaForCareDays.getCareDaysCountMap();
        DateTimeInterval dateTimeInterval = new DateTimeInterval(shift.getStartDate(), shift.getEndDate());
        for (ShiftActivityDTO shiftActivityDTO : shift.getActivities()) {
            if (careDayCountMap.containsKey(shiftActivityDTO.getActivityId())) {
                Optional<CutOffInterval> cutOffIntervalOptional = shiftActivityDTO.getActivity().getRulesActivityTab().getCutOffIntervals().stream().filter(interval -> ((interval.getStartDate().isBefore(shiftDate) || interval.getStartDate().isEqual(shiftDate)) && (interval.getEndDate().isAfter(shiftDate) || interval.getEndDate().isEqual(shiftDate)))).findAny();
                if (cutOffIntervalOptional.isPresent()) {
                    CutOffInterval cutOffInterval = cutOffIntervalOptional.get();
                    dateTimeInterval = dateTimeInterval.addInterval(new DateTimeInterval(DateUtils.asDate(cutOffInterval.getStartDate()), DateUtils.asDate(cutOffInterval.getEndDate().plusDays(1))));
                }
            }
        }
        return dateTimeInterval;
    }

    public static DateTimeInterval getIntervalByActivity(Map<BigInteger, ActivityWrapper> activityWrapperMap, Date shiftStartDate, List<BigInteger> activityIds) {
        LocalDate shiftDate = DateUtils.asLocalDate(shiftStartDate);
        DateTimeInterval dateTimeInterval = new DateTimeInterval(shiftStartDate, DateUtils.asDate(shiftDate.plusDays(1)));
        for (BigInteger activityId : activityIds) {
            if (activityWrapperMap.containsKey(activityId)) {
                Activity activity = activityWrapperMap.get(activityId).getActivity();
                Optional<CutOffInterval> cutOffIntervalOptional = activity.getRulesActivityTab().getCutOffIntervals().stream().filter(interval -> ((interval.getStartDate().isBefore(shiftDate) || interval.getStartDate().isEqual(shiftDate)) && (interval.getEndDate().isAfter(shiftDate) || interval.getEndDate().isEqual(shiftDate)))).findAny();
                if (cutOffIntervalOptional.isPresent()) {
                    CutOffInterval cutOffInterval = cutOffIntervalOptional.get();
                    dateTimeInterval = dateTimeInterval.addInterval(new DateTimeInterval(DateUtils.asDate(cutOffInterval.getStartDate()), DateUtils.asDate(cutOffInterval.getEndDate().plusDays(1))));
                }
            }
        }
        return dateTimeInterval;
    }


    public static boolean isValidForDay(List<Long> dayTypeIds, RuleTemplateSpecificInfo infoWrapper) {
        DayOfWeek shiftDay = DateUtils.asLocalDate(infoWrapper.getShift().getStartDate()).getDayOfWeek();
        return getValidDays(infoWrapper.getDayTypeMap(), dayTypeIds).stream().filter(day -> day.equals(shiftDay)).findAny().isPresent();
    }

    public static boolean validateVetoAndStopBrickRules(float totalBlockingPoints, int totalVeto, int totalStopBricks) {
        return totalBlockingPoints >= totalVeto * VETO_BLOCKING_POINT + totalStopBricks * STOP_BRICK_BLOCKING_POINT;
    }

    public static CareDaysDTO getCareDays(List<CareDaysDTO> careDaysDTOS, int staffAge) {
        CareDaysDTO staffCareDaysDTO = null;
        for (CareDaysDTO careDaysDTO : careDaysDTOS) {
            if (careDaysDTO.getTo() == null && staffAge > careDaysDTO.getFrom()) {
                staffCareDaysDTO = careDaysDTO;
            } else if (isNotNull(careDaysDTO.getTo()) && careDaysDTO.getFrom() <= staffAge && careDaysDTO.getTo() >= staffAge) {
                staffCareDaysDTO = careDaysDTO;
            }
        }
        return staffCareDaysDTO;
    }


    public static void setDayTypeToCTARuleTemplate(StaffAdditionalInfoDTO staffAdditionalInfoDTO) {
        Map<Long, List<Day>> daytypesMap = staffAdditionalInfoDTO.getDayTypes().stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getValidDays()));
        staffAdditionalInfoDTO.getUnitPosition().getCtaRuleTemplates().forEach(ctaRuleTemplateDTO -> {
            Set<DayOfWeek> dayOfWeeks = new HashSet<>();
            List<LocalDate> publicHolidays = new ArrayList<>();
            for (Long dayTypeId : ctaRuleTemplateDTO.getDayTypeIds()) {
                List<Day> currentDay = daytypesMap.get(dayTypeId);
                if (currentDay == null) {
                    throwException("error.dayType.notFound", dayTypeId);
                }
                currentDay.forEach(day -> {
                    if (!day.name().equals(EVERYDAY)) {
                        dayOfWeeks.add(DayOfWeek.valueOf(day.name()));
                    } else {
                        dayOfWeeks.addAll(Arrays.asList(DayOfWeek.values()));
                    }
                });
                /*List<LocalDate> publicHoliday = staffAdditionalInfoDTO.getPublicHoliday().get(dayTypeId);
                if (CollectionUtils.isNotEmpty(publicHoliday)) {
                    publicHolidays.addAll(publicHoliday);
                }*/
            }
            ctaRuleTemplateDTO.setPublicHolidays(publicHolidays);
            ctaRuleTemplateDTO.setDays(new ArrayList<>(dayOfWeeks));
        });
    }


}