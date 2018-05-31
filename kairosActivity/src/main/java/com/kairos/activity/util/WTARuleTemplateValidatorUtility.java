package com.kairos.activity.util;

/**
 * @author pradeep
 * @date - 11/5/18
 */


import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.client.dto.staff.StaffAdditionalInfoDTO;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.enums.MinMaxSetting;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.period.PlanningPeriod;
import com.kairos.activity.persistence.model.wta.StaffWTACounter;
import com.kairos.activity.persistence.model.wta.templates.PhaseTemplateValue;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.activity.persistence.model.wta.wrapper.*;
import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.persistence.model.user.country.Day;
import com.kairos.response.dto.web.cta.DayTypeDTO;

import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.activity.constants.AppConstants.*;

/**
 * @author pradeep
 * @date - 10/5/18
 */

public class WTARuleTemplateValidatorUtility {


    


    //MaximumAverageScheduledTimeWTATemplate
    public static int checkConstraints(ShiftWithActivityDTO shift, List<ShiftWithActivityDTO> shifts, AverageScheduledTimeWTATemplate ruleTemplate){
        int totalScheduledTime = 0;//(int) shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumAverageScheduledTimeInfo();
        DateTimeInterval interval = getIntervalByRuleTemplate(shift,ruleTemplate.getIntervalUnit(),ruleTemplate.getIntervalLength());
        for (ShiftWithActivityDTO shift1:shifts) {
            if(interval.overlaps(shift1.getDateTimeInterval())){
                totalScheduledTime+=interval.overlap(shift1.getDateTimeInterval()).getMinutes();
            }
        }
        return totalScheduledTime>ruleTemplate.getMaximumAvgTime()?totalScheduledTime-(int)ruleTemplate.getMaximumAvgTime():0;
    }


    public static int getConsecutiveDays(List<LocalDate> localDates) {
        if(localDates.size()<2) return 0;
        //Collections.sort(localDates);
        int count = 1;
        int max = 0;
        int l=1;
        while(l<localDates.size()){
            if(localDates.get(l-1).equals(localDates.get(l).minusDays(1))){
                count++;
            }else{
                count=0;
            }
            if(count>max){
                max=count;
            }
            l++;
        }
        return max;
    }

    //MaximumConsecutiveWorkingDaysWTATemplate


    //MaximumConsecutiveWorkingNightsWTATemplate
    private static int getConsecutiveNightShifts(Set<LocalDate> localDates, Shift shift){
        int count = 0;
        int i=1;
        LocalDate prevDayOfShift = DateUtils.getZoneDateTime(shift.getStartDate()).toLocalDate().minusDays(i);
        while (true){
            if(localDates.contains(prevDayOfShift)){
                count++;
                i++;
                prevDayOfShift = prevDayOfShift.minusDays(i);
            }else break;
        }
        return count;
    }

    /*public static int checkConstraints(List<ShiftWithActivityDTO> shifts,TimeSlotWrapper timeSlotWrapper){
        if(shifts.size()<2) return  0;
        int count = 0;
        int consecutiveNightCount = 1;
        sortShifts(shifts);
        List<LocalDate> localDates=getSortedDates(shifts);
        for (int i=localDates.size()-1;i>=0;i--){
            if(i!=0){
                if(localDates.get(i-1).equals(localDates.get(i).minusDays(1))  && isNightShift(shifts.get(i),timeSlotWrapper)&& isNightShift(shifts.get(i-1),timeSlotWrapper)){
                    count++;
                }else {
                    count = 0;
                }
            }
            if(consecutiveNightCount<count){
                consecutiveNightCount = count;
            }
        }

        return consecutiveNightCount > nightsWorked?(consecutiveNightCount-(int) nightsWorked):0;
    }*/



    //MaximumNightShiftLengthWTATemplate
    /*public static int checkConstraints(ShiftWithActivityDTO shift,TimeSlotWrapper timeSlotWrapper){
        if(isNightShift(shift,timeSlotWrapper)){
            return !(shift).isAbsenceActivityApplied() && shift.getMinutes() > timeLimit?(shift.getMinutes()-timeLimit):0;
        }
        return 0;
    }*/


    //MinimumConsecutiveNightsWTATemplate
    /*public static int checkConstraints(List<ShiftWithActivityDTO> shifts,TimeSlotWrapper timeSlotWrapper){
        if(shifts.size()<2) return 0;
        int count = 0;
        int consecutiveNightCount=1;
        sortShifts(shifts);
        List<LocalDate> localDates= getSortedDates(shifts);

        for (int i=localDates.size()-1;i>=0;i--){
            if(i!=0){
                if(localDates.get(i-1).equals(localDates.get(i).minusDays(1)) && isNightShift(shifts.get(i),timeSlotWrapper) &&  isNightShift(shifts.get(i-1),timeSlotWrapper)){
                    count++;
                }else {
                    count = 0;
                }
            }
            if(consecutiveNightCount<count){
                consecutiveNightCount = count;
            }
        }
        return consecutiveNightCount < daysLimit?(daysLimit - consecutiveNightCount):0;
    }*/

    // MinimumDailyRestingTimeWTATemplateTemplate
    /*public static void checkConstraints(List<ShiftWithActivityDTO> shifts,DailyRestingTimeWTATemplate ruleTemplate){
        if(shifts.size()>2) {
            List<DateTimeInterval> intervals = getSortedIntervals(shifts);
            int restingTimeUnder = 0;
            for (int i = 1; i < intervals.size(); i++) {
                long lastEnd = intervals.get(i - 1).getEndMillis();
                long thisStart = intervals.get(i).getStartMillis();
                long totalRest = (thisStart - lastEnd) / 60000;
                if (!isValid(ruleTemplate.getMinMaxSetting(), (int) ruleTemplate.getContinuousDayRestHours(), (int) totalRest)) {
                    new InvalidRequestException("");
                }
            }
        }
    }*/


    //MinimumDurationBetweenShiftWTATemplate


    //MinimumRestConsecutiveNightsWTATemplate
    /*public static int checkConstraints(List<ShiftWithActivityDTO> shifts, ConsecutiveRestPartOfDayWTATemplate ruleTemplate,TimeSlotWrapper timeSlotWrapper) {
        if(shifts.size()<2) return 0;
        sortShifts(shifts);
        List<LocalDate> dates=getSortedDates(shifts);

        int l=1;
        int consDays=0;
        int totalRestUnder=0;
        while (l<dates.size()){
            if(dates.get(l-1).equals(dates.get(l).minusDays(1)) && isNightShift(shifts.get(l),timeSlotWrapper)&& isNightShift(shifts.get(l-1),timeSlotWrapper)){
                consDays++;
            }else{
                consDays=0;
            }
            if(consDays>=nightsWorked){
                ZonedDateTime start=DateUtils.getZoneDateTime(shifts.get(l-1).getEndDate());
                ZonedDateTime end=DateUtils.getZoneDateTime(shifts.get(l).getStartDate());
                int diff=new DateTimeInterval(start,end).getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }
        return totalRestUnder;
    }*/

    /*public static boolean isNightShift(ShiftWithActivityDTO shift,TimeSlotWrapper timeSlotWrapper) {
        return getNightTimeInterval().contains(shift.getStart().getMinuteOfDay());
    }*/

    //MinimumRestInConsecutiveDaysWTATemplate
    /*public static int checkConstraints(List<ShiftWithActivityDTO> shifts){
        if(shifts.size()<2) return 0;
        sortShifts(shifts);
        List<LocalDate> dates=getSortedDates(shifts);
        int l=1;
        int consDays=0;
        int totalRestUnder=0;
        while (l<dates.size()){
            if(dates.get(l-1).equals(dates.get(l).minusDays(1))){
                consDays++;
            }else{
                consDays=0;
            }
            if(consDays>=daysWorked){
                ZonedDateTime start=DateUtils.getZoneDateTime(shifts.get(l-1).getEndDate());
                ZonedDateTime end=DateUtils.getZoneDateTime(shifts.get(l).getStartDate());
                int diff=new DateTimeInterval(start,end).getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }
        return totalRestUnder;
    }*/

    //MinimumShiftLengthWTATemplate
    /*public static int checkConstraints(ShiftWithActivityDTO shift){
        return !(shift).isAbsenceActivityApplied() && shift.getMinutes()<timeLimit?((int) timeLimit-shift.getMinutes()):0;
    }*/

    public static List<LocalDate> getSortedDates(List<ShiftWithActivityDTO> shifts){
        List<LocalDate> dates=new ArrayList<>(shifts.stream().map(s->DateUtils.asLocalDate(s.getStartDate())).collect(Collectors.toSet()));
        Collections.sort(dates);
        return dates;
    }

    public static List<DateTimeInterval> getSortedIntervals(List<ShiftWithActivityDTO> shifts){
        List<DateTimeInterval> intervals= new ArrayList<>();
        for(ShiftWithActivityDTO s:sortShifts(shifts)){
            intervals.add(s.getDateTimeInterval());
        }
        return intervals;
    }

    public static List<ShiftWithActivityDTO> sortShifts(List<ShiftWithActivityDTO> shifts){
        shifts.sort(Comparator.comparing(s->s.getStartDate()));
        return shifts;
    }

    public static Comparator getShiftStartTimeComparator() {
        Comparator shiftStartComparator = new Comparator<ShiftWithActivityDTO>() {
            @Override
            public int compare(ShiftWithActivityDTO shift1, ShiftWithActivityDTO shift2) {
                if (shift1.getStartDate() != null && shift2.getStartDate() != null && shift1.getStaffId().equals(shift2.getStaffId())) {
                    return shift1.getStartDate().compareTo(shift2.getStartDate());
                } else {
                    return -1;
                }
            }
        };
        return shiftStartComparator;
    }

    public static boolean isValid(MinMaxSetting minMaxSetting,int limitValue,int calculatedValue){
        return minMaxSetting.equals(MinMaxSetting.MINIMUM)? limitValue < calculatedValue : limitValue > calculatedValue;
    }

    public static List<LocalDate> getSortedAndUniqueDates(List<ShiftWithActivityDTO> shifts, ShiftWithActivityDTO shift){
        List<LocalDate> dates=new ArrayList<LocalDate>(shifts.stream().map(s->DateUtils.asLocalDate(s.getStartDate())).collect(Collectors.toSet()));
        Collections.sort(dates);
        return dates;
    }


    public static List<Integer> getValidDays(List<DayTypeDTO> dayTypeDTOS,List<Long> dayTypeIds){
        Set<Integer> dayOfWeeks = new HashSet<>();
        dayTypeDTOS.forEach(dayTypeDTO -> {
            dayTypeIds.forEach(dayTypeId->{
                if(dayTypeDTO.getId().equals(dayTypeId)){
                    dayTypeDTO.getValidDays().forEach(day -> {
                        if(!day.equals(Day.EVERYDAY)){
                            dayOfWeeks.add(DayOfWeek.valueOf(day.name()).getValue());
                        }if(day.equals(Day.EVERYDAY)){
                            dayOfWeeks.addAll(Arrays.stream(DayOfWeek.values()).map(dayOfWeek -> dayOfWeek.getValue()).collect(Collectors.toList()));
                        }
                    });
                }
            });
        });
        return new ArrayList<>(dayOfWeeks);
    }

   /* public static boolean isValidForPartOfDay(ShiftWithActivityDTO shift, List<PartOfDay> partOfDays, List<TimeSlotWrapper> timeSlotWrappers){
        for (PartOfDay partOfDay:partOfDays){
            switch (partOfDay){
                case DAY: return new TimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).get(ChronoField.MINUTE_OF_DAY),DateUtils.getZoneDateTime(shift.getEndDate()).get(ChronoField.MINUTE_OF_DAY)).overlaps(getTimeSlotByPartOfDay(PartOfDay.DAY.name(),timeSlotWrappers));
                case NIGHT:return new TimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).get(ChronoField.MINUTE_OF_DAY),DateUtils.getZoneDateTime(shift.getEndDate()).get(ChronoField.MINUTE_OF_DAY)).overlaps(getTimeSlotByPartOfDay(PartOfDay.DAY.name(),timeSlotWrappers));
                case EVENING:return new TimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).get(ChronoField.MINUTE_OF_DAY),DateUtils.getZoneDateTime(shift.getEndDate()).get(ChronoField.MINUTE_OF_DAY)).overlaps(getTimeSlotByPartOfDay(PartOfDay.DAY.name(),timeSlotWrappers));
            }
        }
        return false;
    }*/

    public static TimeInterval getTimeSlotByPartOfDay(List<PartOfDay> partOfDays, List<TimeSlotWrapper> timeSlotWrappers,ShiftWithActivityDTO shift){
        TimeInterval timeInterval = null;
        for (PartOfDay partOfDay:partOfDays) {
            for (TimeSlotWrapper timeSlotWrapper : timeSlotWrappers) {
                if (partOfDay.getValue().equals(timeSlotWrapper.getName())) {
                    TimeInterval interval = new TimeInterval(((timeSlotWrapper.getStartHour() * 60) + timeSlotWrapper.getStartMinute()), ((timeSlotWrapper.getEndHour() * 60) + timeSlotWrapper.getEndMinute()));
                    if(interval.contains(DateUtils.getZoneDateTime(shift.getStartDate()).get(ChronoField.MINUTE_OF_DAY))){
                        timeInterval = interval;
                        break;
                    }
                }
            }
        }
        return timeInterval;
    }

    /*public DateTimeInterval addInterval(DateTimeInterval interval1,DateTimeInterval interval2){
        if(interval1.getStart().isAfter(interval2.getStart())){
            interval1 = interval1.withStart(interval2.getStart());
        }
        if(interval1.getEnd().isBefore(interval2.getEnd())){
            interval1 = interval1.withEnd(interval2.getEnd());
        }
        return interval1;
    }*/

    public static DateTimeInterval getIntervalByRuleTemplate(ShiftWithActivityDTO shift, String intervalUnit, long intervalValue){
        DateTimeInterval interval = null;
        switch (intervalUnit){
            case DAYS:interval = new DateTimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).minusDays((int)intervalValue),DateUtils.getZoneDateTime(shift.getEndDate()).plusDays((int)intervalValue));
                break;
            case WEEKS:interval = new DateTimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).minusWeeks((int)intervalValue),DateUtils.getZoneDateTime(shift.getEndDate()).plusWeeks((int)intervalValue));
                break;
            case MONTHS:interval = new DateTimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).minusMonths((int)intervalValue),DateUtils.getZoneDateTime(shift.getEndDate()).plusMonths((int)intervalValue));
                break;
            case YEARS:interval = new DateTimeInterval(DateUtils.getZoneDateTime(shift.getStartDate()).minusYears((int)intervalValue),DateUtils.getZoneDateTime(shift.getEndDate()).plusYears((int)intervalValue));
                break;
        }
        return interval;
    }

    public static List<ShiftWithActivityDTO> getShiftsByInterval(DateTimeInterval dateTimeInterval, List<ShiftWithActivityDTO> shifts, TimeInterval timeInterval){
        List<ShiftWithActivityDTO> updatedShifts = new ArrayList<>();
        shifts.forEach(s->{
            if(dateTimeInterval.contains(s.getStartDate()) && (timeInterval==null || timeInterval.contains(DateUtils.getZoneDateTime(s.getStartDate()).get(ChronoField.MINUTE_OF_DAY)))){
                updatedShifts.add(s);
            }
        });
        return updatedShifts;
    }

    public static DateTimeInterval getIntervalByNumberOfWeeks(ShiftWithActivityDTO shift, int numberOfWeeks, LocalDate validationStartDate){
        LocalDate endDate = validationStartDate.plusWeeks(numberOfWeeks);
        DateTimeInterval dateTimeInterval = new DateTimeInterval(validationStartDate.atStartOfDay(ZoneId.systemDefault()),endDate.atStartOfDay(ZoneId.systemDefault()));
        do {
            validationStartDate = endDate;
            endDate = validationStartDate.plusWeeks(numberOfWeeks);
        }while (dateTimeInterval.contains(shift.getStartDate()));
        return dateTimeInterval;
    }

    public static Integer[] getValueByPhase(RuleTemplateSpecificInfo infoWrapper, List<PhaseTemplateValue> phaseTemplateValues,BigInteger ruleTemplateId){
        Integer[] limitAndCounter = new Integer[2];
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if(infoWrapper.getPhase().equals(phaseTemplateValue.getPhaseName()) && !phaseTemplateValue.isDisabled()){
                limitAndCounter[0] = (int)(infoWrapper.getUser().getStaff() ? phaseTemplateValue.getStaffValue() : phaseTemplateValue.getManagementValue());
                limitAndCounter[1] = getCounterValue(infoWrapper,phaseTemplateValue,ruleTemplateId);
                break;
            }
        }
        return limitAndCounter;
    }

    public static Integer getCounterValue(RuleTemplateSpecificInfo infoWrapper,PhaseTemplateValue phaseTemplateValue,BigInteger ruleTemplateId){
        Integer counterValue = phaseTemplateValue.isOptional() ? phaseTemplateValue.getOptionalFrequency() : null;
        return counterValue!=null ? counterValue - infoWrapper.getCounterMap().getOrDefault(ruleTemplateId,0) : null;

    }

    public static List<ShiftWithActivityDTO> filterShifts(List<ShiftWithActivityDTO> shifts, List<BigInteger> timeTypeIds, List<Long> plannedTimeIds, List<BigInteger> activitieIds){
        Set<ShiftWithActivityDTO> shiftQueryResultWithActivities = new HashSet<>();
        if(timeTypeIds!=null){
            shifts.forEach(s->{
                if(timeTypeIds.contains(s.getActivity().getBalanceSettingsActivityTab().getTimeTypeId())){
                    shiftQueryResultWithActivities.add(s);
                }
            });
        }
        if(plannedTimeIds!=null){
            shifts.forEach(s->{
                if(plannedTimeIds.contains(s.getActivity().getBalanceSettingsActivityTab().getPresenceTypeId())){
                    shiftQueryResultWithActivities.add(s);
                }
            });
        }
        if(activitieIds!=null){
            shifts.forEach(s->{
                if(activitieIds.contains(s.getActivity().getId())){
                    shiftQueryResultWithActivities.add(s);
                }
            });
        }
        return new ArrayList<>(shiftQueryResultWithActivities);
    }

    public static DateTimeInterval getIntervalByRuleTemplates(ShiftWithActivityDTO shift, List<WTABaseRuleTemplate> wtaBaseRuleTemplates){
        DateTimeInterval interval = new DateTimeInterval(shift.getStartDate().getTime(),shift.getEndDate().getTime());
        for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case NUMBER_OF_PARTOFDAY:
                    NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = (NumberOfPartOfDayShiftsWTATemplate)ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift,numberOfPartOfDayShiftsWTATemplate.getIntervalUnit(),numberOfPartOfDayShiftsWTATemplate.getIntervalLength()));


                    break;
                case DAYS_OFF_IN_PERIOD:
                    DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = (DaysOffInPeriodWTATemplate)ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift,daysOffInPeriodWTATemplate.getIntervalUnit(),daysOffInPeriodWTATemplate.getIntervalLength()));

                    break;
                case AVERAGE_SHEDULED_TIME:
                    AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = (AverageScheduledTimeWTATemplate)ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift,averageScheduledTimeWTATemplate.getIntervalUnit(),averageScheduledTimeWTATemplate.getIntervalLength()));
                    break;
                case VETO_PER_PERIOD:
                    VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = (VetoPerPeriodWTATemplate)ruleTemplate;
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift,vetoPerPeriodWTATemplate.getNumberOfWeeks(),vetoPerPeriodWTATemplate.getValidationStartDate()));

                    break;
                case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                    NumberOfWeekendShiftsInPeriodWTATemplate numberOfWeekendShiftsInPeriodWTATemplate = (NumberOfWeekendShiftsInPeriodWTATemplate)ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift,numberOfWeekendShiftsInPeriodWTATemplate.getIntervalUnit(),numberOfWeekendShiftsInPeriodWTATemplate.getIntervalLength()));

                    break;
                case WEEKLY_REST_PERIOD:
                    WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate = (WeeklyRestPeriodWTATemplate)ruleTemplate;
                    DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(shift,weeklyRestPeriodWTATemplate.getIntervalUnit(),weeklyRestPeriodWTATemplate.getIntervalLength());
                    dateTimeInterval.setStart(dateTimeInterval.getStart().minusDays(1));
                    dateTimeInterval.setEnd(dateTimeInterval.getEnd().plusDays(1));
                    interval = interval.addInterval(dateTimeInterval);

                    break;
                case SHORTEST_AND_AVERAGE_DAILY_REST:
                    ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = (ShortestAndAverageDailyRestWTATemplate)ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift,shortestAndAverageDailyRestWTATemplate.getIntervalUnit(),shortestAndAverageDailyRestWTATemplate.getIntervalLength()));

                    break;
                case NUMBER_OF_SHIFTS_IN_INTERVAL:
                    ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate = (ShiftsInIntervalWTATemplate)ruleTemplate;
                    interval = interval.addInterval(getIntervalByRuleTemplate(shift,shiftsInIntervalWTATemplate.getIntervalUnit(),shiftsInIntervalWTATemplate.getIntervalLength()));

                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate)ruleTemplate;
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift,seniorDaysPerYearWTATemplate.getNumberOfWeeks().intValue(),seniorDaysPerYearWTATemplate.getValidationStartDate()));

                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate)ruleTemplate;
                    interval = interval.addInterval(getIntervalByNumberOfWeeks(shift,childCareDaysCheckWTATemplate.getNumberOfWeeks(),childCareDaysCheckWTATemplate.getValidationStartDate()));

                    break;
                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }
        }
        return interval;
    }

    public static RuleTemplateSpecificInfo getRuleTemplateSpecificInfo(StaffAdditionalInfoDTO staffAdditionalInfoDTO, ShiftWithActivityDTO shift, List<ShiftWithActivityDTO> shifts, String phase, PlanningPeriod planningPeriod, List<StaffWTACounter> staffWTACounters){
        Map<BigInteger,Integer> staffWTACounterMap = staffWTACounters.stream().collect(Collectors.toMap(sc->sc.getRuleTemplateId(),sc->sc.getCount()));
        return new RuleTemplateSpecificInfo(shifts,shift,staffAdditionalInfoDTO.getTimeSlotSets(),phase,new DateTimeInterval(DateUtils.asDate(planningPeriod.getStartDate()).getTime(),DateUtils.asDate(planningPeriod.getEndDate()).getTime()),staffWTACounterMap,staffAdditionalInfoDTO.getDayTypes(),staffAdditionalInfoDTO.getUser());
    }

    public static List<DayOfWeek> getDayOfWeekByDayType(List<Long> dayTypeIds,List<DayTypeDTO> dayTypeDTOS){
        Set<DayOfWeek> dayOfWeeks = new HashSet<>(dayTypeIds.size());
        dayTypeDTOS.forEach(dayTypeDTO -> {
            if(dayTypeIds.contains(dayTypeDTO.getId())){
                dayOfWeeks.addAll(dayTypeDTO.getValidDays().stream().map(day -> DayOfWeek.valueOf(day.name())).collect(Collectors.toList()));
            }
        });
        return new ArrayList<>(dayOfWeeks);
    }

    public static boolean isValidForDay(List<Long> dayTypeIds,RuleTemplateSpecificInfo infoWrapper){
        int shiftDay = DateUtils.getZoneDateTime(infoWrapper.getShift().getStartDate()).get(ChronoField.DAY_OF_WEEK);
        Optional<DayOfWeek> dayOfWeek = getDayOfWeekByDayType(dayTypeIds,infoWrapper.getDayTypes()).stream().filter(day -> day.getValue()== shiftDay).findAny();
        return dayOfWeek.isPresent();
    }




}