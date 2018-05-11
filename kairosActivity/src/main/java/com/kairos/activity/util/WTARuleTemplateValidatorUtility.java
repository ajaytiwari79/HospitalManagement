package com.kairos.activity.util;

/**
 * @author pradeep
 * @date - 11/5/18
 */


import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.persistence.enums.PartOfDay;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.model.wta.templates.PhaseTemplateValue;
import com.kairos.activity.persistence.model.wta.templates.template_types.*;
import com.kairos.persistence.model.user.country.Day;
import com.kairos.response.dto.web.cta.DayTypeDTO;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pradeep
 * @date - 10/5/18
 */

public class WTARuleTemplateValidatorUtility {


    


    //MaximumAverageScheduledTimeWTATemplate
    public static int checkConstraints(List<Shift> shifts,AverageScheduledTimeWTATemplate ruleTemplate){
        int totalScheduledTime = 0;//(int) shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumAverageScheduledTimeInfo();
        for (Shift shift:shifts) {
            if(interval.overlaps(shift.getInterval())){
                totalScheduledTime+=interval.overlap(shift.getInterval()).toPeriod().getMinutes();
            }
        }
        return totalScheduledTime>maximumAvgTime?totalScheduledTime-(int)maximumAvgTime:0;
    }

    //MaximumConsecutiveWorkingDaysWTATemplate
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

    public static int checkConstraints(List<Shift> shifts, ConsecutiveWorkWTATemplate ruleTemplate) {
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

    public static int checkConstraints(List<Shift> shifts){
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
    public static int checkConstraints(List<Shift> shifts, DaysOffInPeriodWTATemplate ruleTemplate){
        int shiftsNum=getSortedDates(shifts).size();
        return 7-shiftsNum>daysLimit?0:(daysLimit-(7 - shiftsNum));
    }

    //MaximumNightShiftLengthWTATemplate
    public static int checkConstraints(Shift shift){
        if(isNightShift(shift)){
            return !((ShiftRequestPhase)shift).isAbsenceActivityApplied() && shift.getMinutes() > timeLimit?(shift.getMinutes()-timeLimit):0;
        }
        return 0;
    }

    //MaximumNumberOfNightsWTATemplate
    public static int checkConstraints(List<Shift> shifts, NumberOfPartOfDayShiftsWTATemplate ruleTemplate){
        if(shifts.size()<0) return 0;
        int count = (int)shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumNumberOfNightsInfo();
        for (Shift shift:shifts){
            if(isNightShift(shift)){
                count++;
            }
        }
        return count > nightsWorked?(count- nightsWorked):0;
    }


    //MaximumShiftLengthWTATemplate
    public static int checkConstraints(Shift shift, ShiftLengthWTATemplate ruleTemplate){
        return !shift.isAbsenceActivityApplied()&& shift.getMinutes()>timeLimit?(shift.getMinutes()-(int)timeLimit):0;
    }

    //MaximumShiftsInIntervalWTATemplate
    public int checkConstraints(List<Shift> shifts){
        int shiftCount = 0;
        for (Shift shift:shifts) {
            if(interval.contains(shift.getStartDate()))
                shiftCount++;
        }
        return shiftCount>shiftsLimit?(shiftCount-(int)shiftsLimit):0;
    }

    //MinimumConsecutiveNightsWTATemplate
    public static int checkConstraints(List<Shift> shifts){
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
    public static int checkConstraints(List<Shift> shifts,DailyRestingTimeWTATemplate dailyRestingTimeWTATemplate){
        if(shifts.size()<2) return 0;
        List<Interval> intervals=getSortedIntervals(shifts);
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
    public static boolean checkConstraints(List<Shift> shifts, Shift shift,DurationBetweenShiftsWTATemplate ruleTemplate) {
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
    public static int checkConstraints(List<Shift> shifts, ConsecutiveRestPartOfDayWTATemplate ruleTemplate) {
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
                int diff=new Interval(start,end).toDuration().toStandardMinutes().getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }
        return totalRestUnder;
    }

    public boolean isNightShift(Shift shift) {
        return getNightTimeInterval().contains(shift.getStart().getMinuteOfDay());
    }

    //MinimumRestInConsecutiveDaysWTATemplate
    public static int checkConstraints(List<Shift> shifts){
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
                int diff=new Interval(start,end).toDuration().toStandardMinutes().getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }
        return totalRestUnder;
    }

    //MinimumShiftLengthWTATemplate
    public static int checkConstraints(Shift shift){
        return !((ShiftRequestPhase)shift).isAbsenceActivityApplied() && shift.getMinutes()<timeLimit?((int) timeLimit-shift.getMinutes()):0;
    }

    //MinimumWeeklyRestPeriodWTATemplate
    public static int checkConstraints(List<Shift> shifts,WeeklyRestPeriodWTATemplate ruleTemplate){
        if(shifts.size()<2) return 0;
        int totalRestTime = interval.toPeriod().getMinutes();
        for (Shift shift:shifts) {
            totalRestTime-=shift.getMinutes();
        }
        return totalRestTime<continuousWeekRest?(totalRestTime-(int)continuousWeekRest):0;
    }


    //NumberOfWeekendShiftInPeriodWTATemplate
    public static int checkConstraints(List<Shift> shifts,NumberOfWeekendShiftsInPeriodWTATemplate ruleTemplate){
        int weekendShifts=(int) shifts.stream().filter(s->interval.contains(s.getStartDate())).count();
        return weekendShifts>numberShiftsPerPeriod?weekendShifts-numberShiftsPerPeriod:0;
    }


    //ShortestAndAverageDailyRestWTATemplate
    public static int checkConstraints(List<Shift> shifts,ShortestAndAverageDailyRestWTATemplate ruleTemplate){
        if(shifts.size()<2) return 0;
        List<Interval> intervals= getSortedIntervals(shifts);
        int restingTimeUnder=0;
        int totalRestAllShifts=0;
        for(int i=1;i<intervals.size();i++){
            DateTime lastEnd=intervals.get(i-1).getEnd();
            DateTime thisStart=intervals.get(i).getStart();
            long totalRest=(thisStart.getMillisOfDay()-lastEnd.getMillis())/60000;
            restingTimeUnder=(int)(continuousDayRestingTime >totalRest? continuousDayRestingTime -totalRest:0);
            totalRestAllShifts+=totalRest;
        }
        float averageRestingTime=totalRestAllShifts/shifts.size();
        return  (restingTimeUnder + (int)(averageRest>averageRestingTime?averageRest-averageRestingTime:0));
    }

    public static List<LocalDate> getSortedDates(List<Shift> shifts){
        List<LocalDate> dates=new ArrayList<>(shifts.stream().map(s->DateUtils.asLocalDate(s.getStartDate())).collect(Collectors.toSet()));
        Collections.sort(dates);
        return dates;
    }

    public static List<Interval> getSortedIntervals(List<Shift> shifts){
        List<Interval> intervals= new ArrayList<>();
        for(Shift s:sortShifts(shifts)){
            intervals.add(s.getInterval());
        }
        return intervals;
    }

    public static List<Shift> sortShifts(List<Shift> shifts){
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

    public static List<LocalDate> getSortedAndUniqueDates(List<Shift> shifts){
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

    public getValueByPhase(Phase phase, List<PhaseTemplateValue> phaseTemplateValues,){
        phaseTemplateValues.forEach(p->{
            if(p.getStaffValue())
        });
    }

}