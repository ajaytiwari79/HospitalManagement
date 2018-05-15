package com.kairos.activity.util;

/**
 * @author pradeep
 * @date - 11/5/18
 */


import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.model.wta.templates.PhaseTemplateValue;
import com.kairos.activity.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.persistence.model.user.country.Day;
import com.kairos.response.dto.web.cta.DayTypeDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.activity.constants.AppConstants.*;
import static com.kairos.activity.persistence.enums.WTATemplateType.*;

/**
 * @author pradeep
 * @date - 10/5/18
 */

public class WTARuleTemplateValidatorUtility {


    


    //MaximumAverageScheduledTimeWTATemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts, AverageScheduledTimeWTATemplate ruleTemplate){
        int totalScheduledTime = 0;//(int) shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumAverageScheduledTimeInfo();
        for (ShiftQueryResultWithActivity shift:shifts) {
            if(interval.overlaps(shift.getInterval())){
                totalScheduledTime+=interval.overlap(shift.getInterval()).toPeriod().getMinutes();
            }
        }
        return totalScheduledTime>maximumAvgTime?totalScheduledTime-(int)maximumAvgTime:0;
    }


    public static int getConsecutiveDays(List<LocalDate> localDates) {
        if(localDates.size()<2) return 0;
        Collections.sort(localDates);
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
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts,ShiftQueryResultWithActivity shift, ConsecutiveWorkWTATemplate ruleTemplate) {
        int consecutiveDays = getConsecutiveDays(getSortedAndUniqueDates(shifts));
        return consecutiveDays > daysLimit?(consecutiveDays-(int) daysLimit):0;
    }

    //MaximumConsecutiveWorkingNightsWTATemplate
    private static int getConsecutiveNightShifts(Set<LocalDate> localDates, Shift shift){
        int count = 0;
        int i=1;
        LocalDate prevDayOfShift = shift.getStartDate().toLocalDate().minusDays(i);
        while (true){
            if(localDates.contains(prevDayOfShift)){
                count++;
                i++;
                prevDayOfShift = prevDayOfShift.minusDays(i);
            }else break;
        }
        return count;
    }

    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts){
        if(shifts.size()<2) return  0;
        int count = 0;
        int consecutiveNightCount = 1;
        sortShifts(shifts);
        List<LocalDate> localDates=getSortedDates(shifts);
        for (int i=localDates.size()-1;i>=0;i--){
            if(i!=0){
                if(localDates.get(i-1).equals(localDates.get(i).minusDays(1))  && isNightShift(shifts.get(i))&& isNightShift(shifts.get(i-1))){
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
    }

    //MaximumDaysOffInPeriodWTATemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts, DaysOffInPeriodWTATemplate ruleTemplate){
        int shiftsNum=getSortedDates(shifts).size();
        return 7-shiftsNum>daysLimit?0:(daysLimit-(7 - shiftsNum));
    }

    //MaximumNightShiftLengthWTATemplate
    public static int checkConstraints(ShiftQueryResultWithActivity shift){
        if(isNightShift(shift)){
            return !(shift).isAbsenceActivityApplied() && shift.getMinutes() > timeLimit?(shift.getMinutes()-timeLimit):0;
        }
        return 0;
    }

    //MaximumNumberOfNightsWTATemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts, NumberOfPartOfDayShiftsWTATemplate ruleTemplate){
        if(shifts.size()<0) return 0;
        int count = (int)shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumNumberOfNightsInfo();
        for (ShiftQueryResultWithActivity shift:shifts){
            if(isNightShift(shift)){
                count++;
            }
        }
        return count > nightsWorked?(count- nightsWorked):0;
    }


    //MaximumShiftLengthWTATemplate
    public static int checkConstraints(ShiftQueryResultWithActivity shift, ShiftLengthWTATemplate ruleTemplate){
        return !shift.isAbsenceActivityApplied()&& shift.getMinutes()>timeLimit?(shift.getMinutes()-(int)timeLimit):0;
    }

    //MaximumShiftsInIntervalWTATemplate
    public int checkConstraints(List<ShiftQueryResultWithActivity> shifts){
        int shiftCount = 0;
        for (ShiftQueryResultWithActivity shift:shifts) {
            if(interval.contains(shift.getStartDate()))
                shiftCount++;
        }
        return shiftCount>shiftsLimit?(shiftCount-(int)shiftsLimit):0;
    }

    //MinimumConsecutiveNightsWTATemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts){
        if(shifts.size()<2) return 0;
        int count = 0;
        int consecutiveNightCount=1;
        sortShifts(shifts);
        List<LocalDate> localDates= getSortedDates(shifts);

        for (int i=localDates.size()-1;i>=0;i--){
            if(i!=0){
                if(localDates.get(i-1).equals(localDates.get(i).minusDays(1)) && isNightShift(shifts.get(i)) &&  isNightShift(shifts.get(i-1))){
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
    }

    // MinimumDailyRestingTimeWTATemplateTemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts,DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate){
        if(shifts.size()<2) return 0;
        List<DateTimeInterval> intervals=getSortedIntervals(shifts);
        int restingTimeUnder=0;
        for(int i=1;i<intervals.size();i++){
            DateTime lastEnd=intervals.get(i-1).getEndDate();
            DateTime thisStart=intervals.get(i).getStartDate();
            long totalRest=(thisStart.getMillis()-lastEnd.getMillis())/60000;
            restingTimeUnder=(int)(dailyRestingTime >totalRest? dailyRestingTime -totalRest:0);//TODO do we need to verify if shifts overlap. Not needed but possible while it'
        }

        return restingTimeUnder;
    }


    //MinimumDurationBetweenShiftWTATemplate
    public static boolean checkConstraints(List<ShiftQueryResultWithActivity> shifts, ShiftQueryResultWithActivity shift,DurationBetweenShiftsWTATemplate ruleTemplate) {
        boolean isValid = false;
        int timefromPrevShift = 0;
        shifts = (List<Shift>) shifts.stream().filter(shift1 -> shift1.getStartDate() != null && shift1.getEndDate() != null).filter(shift1 -> shift1.getEndDate().isBefore(shift.getStartDate())).sorted(getShiftStartTimeComparator()).collect(Collectors.toList());
        if (shifts.size() > 0) {
            DateTime prevShiftEnd = new DateTime(shifts.size() > 1 ? shifts.get(shifts.size() - 1).getEndDate() : shifts.get(0).getEndDate());
            timefromPrevShift = new Period(prevShiftEnd, new DateTime(shift.getStartDate())).getMinutes();
            if(timefromPrevShift==0 && shift.getStartDate().getDayOfWeek()==1){
                timefromPrevShift = new Period(shift.getEmployee().getPrevShiftEnd(), shift.getStartDate()).getMinutes();
            }
        }
        if (timefromPrevShift < minimumDurationBetweenShifts) {
            isValid = true;
        }
        return isValid;
    }

    //MinimumRestConsecutiveNightsWTATemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts, ConsecutiveRestPartOfDayWTATemplate ruleTemplate) {
        if(shifts.size()<2) return 0;
        sortShifts(shifts);
        List<LocalDate> dates=getSortedDates(shifts);

        int l=1;
        int consDays=0;
        int totalRestUnder=0;
        while (l<dates.size()){
            if(dates.get(l-1).equals(dates.get(l).minusDays(1)) && isNightShift(shifts.get(l))&& isNightShift(shifts.get(l-1))){
                consDays++;
            }else{
                consDays=0;
            }
            if(consDays>=nightsWorked){
                DateTime start=new DateTime(shifts.get(l-1).getEndDate());
                DateTime end=new DateTime(shifts.get(l).getStartDate());
                int diff=new DateTimeInterval(start,end).toDuration().toStandardMinutes().getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }
        return totalRestUnder;
    }

    public static boolean isNightShift(ShiftQueryResultWithActivity shift,TimeSlotWrapper timeSlotWrapper) {
        return getNightTimeInterval().contains(shift.getStart().getMinuteOfDay());
    }

    //MinimumRestInConsecutiveDaysWTATemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts){
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
                DateTime start=new DateTime(shifts.get(l-1).getEndDate());
                DateTime end=new DateTime(shifts.get(l).getStartDate());
                int diff=new DateTimeInterval(start,end).toDuration().toStandardMinutes().getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }
        return totalRestUnder;
    }

    //MinimumShiftLengthWTATemplate
    public static int checkConstraints(ShiftQueryResultWithActivity shift){
        return !(shift).isAbsenceActivityApplied() && shift.getMinutes()<timeLimit?((int) timeLimit-shift.getMinutes()):0;
    }

    //MinimumWeeklyRestPeriodWTATemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts,WeeklyRestPeriodWTATemplate ruleTemplate){
        if(shifts.size()<2) return 0;
        int totalRestTime = interval.getMinutes();
        for (ShiftQueryResultWithActivity shift:shifts) {
            totalRestTime-=shift.getMinutes();
        }
        return totalRestTime<continuousWeekRest?(totalRestTime-(int)continuousWeekRest):0;
    }


    //NumberOfWeekendShiftInPeriodWTATemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts,NumberOfWeekendShiftsInPeriodWTATemplate ruleTemplate){
        int weekendShifts=(int) shifts.stream().filter(s->interval.contains(s.getStartDate())).count();
        return weekendShifts>numberShiftsPerPeriod?weekendShifts-numberShiftsPerPeriod:0;
    }


    //ShortestAndAverageDailyRestWTATemplate
    public static int checkConstraints(List<ShiftQueryResultWithActivity> shifts,ShortestAndAverageDailyRestWTATemplate ruleTemplate){
        if(shifts.size()<2) return 0;
        List<DateTimeInterval> intervals= getSortedIntervals(shifts);
        int restingTimeUnder=0;
        int totalRestAllShifts=0;
        for(int i=1;i<intervals.size();i++){
            ZonedDateTime lastEnd=intervals.get(i-1).getEnd();
            ZonedDateTime thisStart=intervals.get(i).getStart();
            long totalRest=(thisStart.getMillisOfDay()-lastEnd.getMillis())/60000;
            restingTimeUnder=(int)(continuousDayRestingTime >totalRest? continuousDayRestingTime -totalRest:0);
            totalRestAllShifts+=totalRest;
        }
        float averageRestingTime=totalRestAllShifts/shifts.size();
        return  (restingTimeUnder + (int)(averageRest>averageRestingTime?averageRest-averageRestingTime:0));
    }

    public static List<LocalDate> getSortedDates(List<ShiftQueryResultWithActivity> shifts){
        List<LocalDate> dates=new ArrayList<>(shifts.stream().map(s->DateUtils.asJodaLocalDate(s.getStartDate())).collect(Collectors.toSet()));
        Collections.sort(dates);
        return dates;
    }

    public static List<DateTimeInterval> getSortedIntervals(List<ShiftQueryResultWithActivity> shifts){
        List<DateTimeInterval> intervals= new ArrayList<>();
        for(ShiftQueryResultWithActivity s:sortShifts(shifts)){
            intervals.add(s.getInterval());
        }
        return intervals;
    }

    public static List<ShiftQueryResultWithActivity> sortShifts(List<ShiftQueryResultWithActivity> shifts){
        shifts.sort(Comparator.comparing(Shift::getStartDate));
        return shifts;
    }

    public static Comparator getShiftStartTimeComparator() {
        Comparator shiftStartComparator = new Comparator<Shift>() {
            @Override
            public int compare(Shift shift1, Shift shift2) {
                if (shift1.getStartDate() != null && shift2.getStartDate() != null && shift1.getStaffId().equals(shift2.getStaffId())) {
                    return shift1.getStartDate().compareTo(shift2.getStartDate());
                } else {
                    return -1;
                }
            }
        };
        return shiftStartComparator;
    }

    public static List<LocalDate> getSortedAndUniqueDates(List<ShiftQueryResultWithActivity> shifts){
        List<LocalDate> dates=new ArrayList<LocalDate>(shifts.stream().map(s->DateUtils.asJodaLocalDate(s.getStartDate())).collect(Collectors.toSet()));
        Collections.sort(dates);
        return dates;
    }


    public static List<Integer> getValidDays(List<DayTypeDTO> dayTypeDTOS,List<Long> dayTypeIds){
        Set<Integer> dayOfWeeks = new HashSet<>();
        dayTypeDTOS.forEach(dayTypeDTO -> {
            dayTypeIds.forEach(dayTypeId->{
                if(dayTypeDTO.getId().equals(dayTypeId)){
                    dayOfWeeks.addAll(dayTypeDTO.getValidDays().stream().filter(day -> !day.equals(Day.EVERYDAY)).map(day -> DayOfWeek.valueOf(day.name()).getValue()).collect(Collectors.toList()));
                }
            });
        });
        return new ArrayList<>(dayOfWeeks);
    }

    public static boolean isValidForPartOfDay(Shift shift, List<PartOfDay> partOfDays, List<TimeSlotWrapper> timeSlotWrappers){
        for (PartOfDay partOfDay:partOfDays){
            switch (partOfDay){
                case DAY: return new TimeInterval(new DateTime(shift.getStartDate()).getMinuteOfDay(),new DateTime(shift.getEndDate()).getMinuteOfDay()).overlaps(getTimeSlotByPartOfDay(PartOfDay.DAY.name(),timeSlotWrappers));
                case NIGHT:return new TimeInterval(new DateTime(shift.getStartDate()).getMinuteOfDay(),new DateTime(shift.getEndDate()).getMinuteOfDay()).overlaps(getTimeSlotByPartOfDay(PartOfDay.DAY.name(),timeSlotWrappers));
                case EVENING:return new TimeInterval(new DateTime(shift.getStartDate()).getMinuteOfDay(),new DateTime(shift.getEndDate()).getMinuteOfDay()).overlaps(getTimeSlotByPartOfDay(PartOfDay.DAY.name(),timeSlotWrappers));
            }
        }
        return false;
    }

    public static TimeInterval getTimeSlotByPartOfDay(String partOfDay, List<TimeSlotWrapper> timeSlotWrappers){
        TimeInterval timeInterval = null;
        for (TimeSlotWrapper timeSlotWrapper:timeSlotWrappers){
            if(partOfDay.equals(timeSlotWrapper.getName())){
                timeInterval = new TimeInterval(((timeSlotWrapper.getStartHour()*60)+timeSlotWrapper.getStartMinute()),((timeSlotWrapper.getEndHour()*60)+timeSlotWrapper.getEndMinute()));
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

    public DateTimeInterval getIntervalByRuleTemplate(Shift shift,String intervalUnit,long intervalValue){
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

    public DateTimeInterval getIntervalByNumberOfWeeks(Shift shift, int numberOfWeeks, LocalDate validationStartDate){
        LocalDate endDate = validationStartDate.plusWeeks(numberOfWeeks);
        DateTimeInterval dateTimeInterval = new DateTimeInterval(validationStartDate.atStartOfDay(ZoneId.systemDefault()),endDate.atStartOfDay(ZoneId.systemDefault()));
        do {
            validationStartDate = endDate;
            endDate = validationStartDate.plusWeeks(numberOfWeeks);
        }while (dateTimeInterval.contains(shift.getStartDate()));
        return dateTimeInterval;
    }

    /*public getValueByPhase(Phase phase, List<PhaseTemplateValue> phaseTemplateValues){
        phaseTemplateValues.forEach(p->{
            if(p.getStaffValue())
        });
    }*/

    public DateTimeInterval getIntervalByRuleTemplates(Shift shift, List<WTABaseRuleTemplate> wtaBaseRuleTemplates){
        DateTimeInterval interval = new DateTimeInterval(shift.getStartDate().getTime(),shift.getEndDate().getTime());
        for (WTABaseRuleTemplate ruleTemplate : wtaBaseRuleTemplates) {
            switch (ruleTemplate.getWtaTemplateType()) {
                case NUMBER_OF_PARTOFDAY:
                    NumberOfPartOfDayShiftsWTATemplate numberOfPartOfDayShiftsWTATemplate = (NumberOfPartOfDayShiftsWTATemplate)ruleTemplate;
                    interval.addInterval(getIntervalByRuleTemplate(shift,numberOfPartOfDayShiftsWTATemplate.getIntervalUnit(),numberOfPartOfDayShiftsWTATemplate.getIntervalLength());

                    break;
                case DAYS_OFF_IN_PERIOD:
                    DaysOffInPeriodWTATemplate daysOffInPeriodWTATemplate = (DaysOffInPeriodWTATemplate)ruleTemplate;
                    interval = getIntervalByRuleTemplate(shift,daysOffInPeriodWTATemplate.getIntervalUnit(),daysOffInPeriodWTATemplate.getIntervalLength());

                    break;
                case AVERAGE_SHEDULED_TIME:
                    AverageScheduledTimeWTATemplate averageScheduledTimeWTATemplate = (AverageScheduledTimeWTATemplate)ruleTemplate;
                    interval = getIntervalByRuleTemplate(shift,averageScheduledTimeWTATemplate.getIntervalUnit(),averageScheduledTimeWTATemplate.getIntervalLength());
                    break;
                case VETO_PER_PERIOD:
                    VetoPerPeriodWTATemplate vetoPerPeriodWTATemplate = (VetoPerPeriodWTATemplate)ruleTemplate;
                    interval = getIntervalByNumberOfWeeks(shift,vetoPerPeriodWTATemplate.getNumberOfWeeks(),vetoPerPeriodWTATemplate.getValidationStartDate());

                    break;
                case NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD:
                    NumberOfWeekendShiftsInPeriodWTATemplate numberOfWeekendShiftsInPeriodWTATemplate = (NumberOfWeekendShiftsInPeriodWTATemplate)ruleTemplate;
                    interval = getIntervalByRuleTemplate(shift,numberOfWeekendShiftsInPeriodWTATemplate.getIntervalUnit(),numberOfWeekendShiftsInPeriodWTATemplate.getIntervalLength());

                    break;
                case WEEKLY_REST_PERIOD:
                    WeeklyRestPeriodWTATemplate weeklyRestPeriodWTATemplate = (WeeklyRestPeriodWTATemplate)ruleTemplate;
                    interval = getIntervalByRuleTemplate(shift,weeklyRestPeriodWTATemplate.getIntervalUnit(),weeklyRestPeriodWTATemplate.getIntervalLength());

                    break;
                case SHORTEST_AND_AVERAGE_DAILY_REST:
                    ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = (ShortestAndAverageDailyRestWTATemplate)ruleTemplate;
                    interval = getIntervalByRuleTemplate(shift,shortestAndAverageDailyRestWTATemplate.getIntervalUnit(),shortestAndAverageDailyRestWTATemplate.getIntervalLength());

                    break;
                case NUMBER_OF_SHIFTS_IN_INTERVAL:
                    ShiftsInIntervalWTATemplate shiftsInIntervalWTATemplate = (ShiftsInIntervalWTATemplate)ruleTemplate;
                    interval = getIntervalByRuleTemplate(shift,shiftsInIntervalWTATemplate.getIntervalUnit(),shiftsInIntervalWTATemplate.getIntervalLength());

                    break;
                case SENIOR_DAYS_PER_YEAR:
                    SeniorDaysPerYearWTATemplate seniorDaysPerYearWTATemplate = (SeniorDaysPerYearWTATemplate)ruleTemplate;
                    interval = getIntervalByNumberOfWeeks(shift,seniorDaysPerYearWTATemplate.getNumberOfWeeks().intValue(),seniorDaysPerYearWTATemplate.getValidationStartDate());

                    break;
                case CHILD_CARE_DAYS_CHECK:
                    ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate)ruleTemplate;
                    interval = getIntervalByNumberOfWeeks(shift,childCareDaysCheckWTATemplate.getNumberOfWeeks(),childCareDaysCheckWTATemplate.getValidationStartDate());

                    break;
                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }
        }


    }

}