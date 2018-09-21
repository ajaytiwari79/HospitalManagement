package com.planner.service.shiftPlanningService;


import com.kairos.shiftplanning.domain.PrevShiftsInfo;
import com.kairos.shiftplanning.domain.wta.*;
import com.planner.domain.staff.PlanningShift;
import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShiftWtaInfoService {

    private static Logger logger = LoggerFactory.getLogger(ShiftWtaInfoService.class);

    public PrevShiftsInfo getPrevShiftsInfo(List<PlanningShift> shifts, WorkingTimeConstraints workingTimeConstraints){
        shifts = generatePrevShifts();
        PrevShiftsInfo prevShiftsInfo = new PrevShiftsInfo();
        prevShiftsInfo.setMaximumAverageScheduledTimeInfo(getMaximumAverageScheduledTimeInfo(shifts,workingTimeConstraints.getMaximumAverageScheduledTime()));
        prevShiftsInfo.setMaximumDaysOffInPeriodInfo(getMaximumDaysOffInPeriodInfo(shifts,workingTimeConstraints.getMaximumDaysOffInPeriod()));
        prevShiftsInfo.setMaximumNumberOfNightsInfo(getMaximumNumberOfNightsInfo(shifts,workingTimeConstraints.getMaximumNumberOfNights()));
        prevShiftsInfo.setMaximumShiftsInIntervalInfo(getMaximumShiftsInIntervalInfo(shifts,workingTimeConstraints.getMaximumShiftsInInterval()));
        prevShiftsInfo.setMaximumSeniorDaysInYearInfo(getMaximumSeniorDaysInYearInfo(shifts,workingTimeConstraints.getMaximumSeniorDaysInYear()));
        prevShiftsInfo.setNumberOfWeekendShiftInPeriodInfo(getNumberOfWeekendShiftInPeriodInfo(shifts,workingTimeConstraints.getNumberOfWeekendShiftInPeriod()));
        prevShiftsInfo.setShortestAndAverageDailyRestInfo(getShortestAndAverageDailyRestInfo(shifts,workingTimeConstraints.getShortestAndAverageDailyRest()));
        prevShiftsInfo.setPrevConsecutiveNightShift(getPrevConsecutiveNightShift(shifts));
        prevShiftsInfo.setPrevConsecutiveWorkingDay(getPrevConsecutiveWorkingDay(shifts));
        return prevShiftsInfo;
    }

    private long getPrevConsecutiveNightShift(List<PlanningShift> shifts){
        long prevConsecutiveNightShift = 0l;
        //DateTime endTime = new DateTime().withDayOfWeek(1).withTimeAtStartOfDay();
        //DateTime startTime = endTime.minusWeeks(1).withTimeAtStartOfDay();
        //Interval interval = new Interval(startTime,endTime);
        getSortedShift(shifts);
        long nightStarts = new DateTime().withTimeAtStartOfDay().plusHours(20).getMinuteOfDay();
        long nightEnds = new DateTime().withTimeAtStartOfDay().plusHours(2).getMinuteOfDay();
        LocalDate startOfWeekLocalDate = new LocalDate().withDayOfWeek(1).minusDays(1);
        for (int i=1;i<shifts.size();i++){
            DateTime shiftStart = shifts.get(shifts.size()-i).getStartDate();
            if(shiftStart.toLocalDate().equals(startOfWeekLocalDate) && shiftStart.getMinuteOfDay()>nightStarts && shiftStart.getMinuteOfDay()<nightEnds){
                prevConsecutiveNightShift+=1;
            }else break;
        }
        return prevConsecutiveNightShift;
    }

    private long getPrevConsecutiveWorkingDay(List<PlanningShift> shifts){
        long prevConsecutiveWorkingDay = 0l;
        getSortedShift(shifts);
        LocalDate startOfWeekLocalDate = new LocalDate().withDayOfWeek(1).minusDays(1);
        for (int i=1;i<shifts.size();i++){
            if(shifts.get(shifts.size()-i).getStartDate().toLocalDate().equals(startOfWeekLocalDate)){
                prevConsecutiveWorkingDay+=1;
            }else break;
        }
        return prevConsecutiveWorkingDay;
    }

    private long getMaximumAverageScheduledTimeInfo(List<PlanningShift> shifts, MaximumAverageScheduledTimeWTATemplate wtaTemplate){
        long avrageRestTime = 0l;
        Interval currentInterval = getInterval(wtaTemplate.getIntervalLength(),"");

        int totalRestTime = currentInterval.toPeriod().getMinutes();
        shifts = shifts.stream().filter(shift1 -> currentInterval.contains(shift1.getEndDate()) || currentInterval.contains(shift1.getStartDate())).collect(Collectors.toList());
        for (PlanningShift shift:shifts){
            if(currentInterval.contains(shift.getStartDate()) && currentInterval.contains(shift.getEndDate())){
                totalRestTime = totalRestTime - new Period(shift.getStartDate(),shift.getEndDate()).getMinutes();
            }else{
                if(!currentInterval.contains(shift.getStartDate()) && currentInterval.contains(shift.getEndDate())){
                    totalRestTime = totalRestTime - new Period(currentInterval.getStart(),shift.getEndDate()).getMinutes();
                }else{
                    totalRestTime = totalRestTime - new Period(shift.getStartDate(),currentInterval.getEnd()).getMinutes();
                }
            }
        }
        avrageRestTime = totalRestTime/7;
        return avrageRestTime;
    }

    private Interval getInterval(long intervalValue,String intervalUnit){
        DateTime endTime = new DateTime().withDayOfWeek(1).withTimeAtStartOfDay();
        DateTime startTime = endTime;
        /*switch (intervalUnit){
            case "Day":startTime = startTime.minusDays((int)intervalValue);
            break;
            case "Week":startTime = startTime.minusWeeks((int) intervalValue);
            break;
            case "Month":startTime = startTime.minusMonths((int)intervalValue);
            break;
            case "Year":startTime = startTime.minusYears((int) intervalValue);
            break;
        }*/
        startTime = startTime.minusWeeks(2).withTimeAtStartOfDay();
        return new Interval(startTime,endTime);
    }

    private long getMaximumDaysOffInPeriodInfo(List<PlanningShift> shifts, MaximumDaysOffInPeriodWTATemplate wtaTemplate){
        long maximumDaysOffInPeriod = 0l;
         return maximumDaysOffInPeriod;
    }

    private long getMaximumNumberOfNightsInfo(List<PlanningShift> shifts, MaximumNumberOfNightsWTATemplate wtaTemplate){
        long numberOfNights = 0l;
        Interval currentInterval = getInterval(wtaTemplate.getIntervalLength(),wtaTemplate.getIntervalUnit());
        long nightStarts = new DateTime().withTimeAtStartOfDay().plusHours(20).getMinuteOfDay();
        long nightEnds = new DateTime().withTimeAtStartOfDay().plusHours(2).getMinuteOfDay();
        numberOfNights = shifts.stream().filter(shift -> currentInterval.contains(shift.getStartDate()) && shift.getStartDate().getMinuteOfDay()>nightStarts && shift.getStartDate().getMinuteOfDay()<nightEnds).count();
        return numberOfNights;
    }

    private long getMaximumShiftsInIntervalInfo(List<PlanningShift> shifts, MaximumShiftsInIntervalWTATemplate wtaTemplate){
        long shiftsInInterval = 0l;
        Interval currentInterval = getInterval(wtaTemplate.getIntervalLength(),wtaTemplate.getIntervalUnit());
        shiftsInInterval = shifts.stream().filter(shift -> currentInterval.contains(shift.getStartDate())).count();
        return shiftsInInterval;

    }

    private long getMaximumSeniorDaysInYearInfo(List<PlanningShift> shifts,MaximumSeniorDaysInYearWTATemplate wtaTemplate){
        long maximumSeniorDaysInYear = 0l;
        return maximumSeniorDaysInYear;
    }

    private long getNumberOfWeekendShiftInPeriodInfo(List<PlanningShift> shifts,NumberOfWeekendShiftInPeriodWTATemplate wtaTemplate){
        long numberOfWeekendShiftInPeriod = 0l;
        numberOfWeekendShiftInPeriod = shifts.stream().filter(shift -> shift.getStartDate().getDayOfWeek()==6 || shift.getStartDate().getDayOfWeek()==7).count();
        return numberOfWeekendShiftInPeriod;
    }

    private long getShortestAndAverageDailyRestInfo(List<PlanningShift> shifts,ShortestAndAverageDailyRestWTATemplate wtaTemplate){
        long avrageRestTime = 0l;
        Interval currentInterval = getInterval(wtaTemplate.getIntervalLength(),wtaTemplate.getIntervalUnit());
        int totalRestTime = currentInterval.toPeriod().getMinutes();
        shifts = shifts.stream().filter(shift1 -> currentInterval.contains(shift1.getEndDate()) || currentInterval.contains(shift1.getStartDate())).collect(Collectors.toList());
        for (PlanningShift shift:shifts){
            if(currentInterval.contains(shift.getStartDate()) && currentInterval.contains(shift.getEndDate())){
                totalRestTime = totalRestTime - new Period(shift.getStartDate(),shift.getEndDate()).getMinutes();
            }else{
                if(!currentInterval.contains(shift.getStartDate()) && currentInterval.contains(shift.getEndDate())){
                    totalRestTime = totalRestTime - new Period(currentInterval.getStart(),shift.getEndDate()).getMinutes();
                }else{
                    totalRestTime = totalRestTime - new Period(shift.getStartDate(),currentInterval.getEnd()).getMinutes();
                }
            }
        }
        avrageRestTime = totalRestTime/7;
        return avrageRestTime;
    }

    private List<PlanningShift> generatePrevShifts(){
        List<PlanningShift> shifts = new ArrayList<>();
        int hour = 5;
        for (int i=0;i<50;i++){
            PlanningShift shift = new PlanningShift();
            //shift.setId(UUID.randomUUID().toString());
            shift.setStartTime(new DateTime().minusHours(hour+5).toDate());
            shift.setEndTime(new DateTime().minusHours(hour).toDate());
            hour +=6;
            shifts.add(shift);
        }
        return shifts;
    }

    private void getSortedShift(List<PlanningShift> planningShifts){
        Collections.sort(planningShifts, new Comparator<PlanningShift>() {
            @Override
            public int compare(PlanningShift o1, PlanningShift o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
    };

}
