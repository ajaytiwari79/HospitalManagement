package com.kairos.shiftplanning.domain.staff;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrevShiftsInfo {

    private long shortestAndAverageDailyRestInfo;//no.of rest min
    private long numberOfWeekendShiftInPeriodInfo;//no. of weekendShift
    private long maximumShiftsInIntervalInfo;//no. of shifts
    private long maximumSeniorDaysInYearInfo;//no. of days
    private long maximumNumberOfNightsInfo;//no. of nights
    private long maximumDaysOffInPeriodInfo;//no. of days
    private long maximumAverageScheduledTimeInfo;//no. of sheduled min
    private long prevConsecutiveNightShift;
    private long prevConsecutiveWorkingDay;

}
