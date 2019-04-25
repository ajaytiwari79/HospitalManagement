package com.kairos.shiftplanning.domain.staff;

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


    public long getPrevConsecutiveNightShift() {
        return prevConsecutiveNightShift;
    }

    public void setPrevConsecutiveNightShift(long prevConsecutiveNightShift) {
        this.prevConsecutiveNightShift = prevConsecutiveNightShift;
    }

    public long getPrevConsecutiveWorkingDay() {
        return prevConsecutiveWorkingDay;
    }

    public void setPrevConsecutiveWorkingDay(long prevConsecutiveWorkingDay) {
        this.prevConsecutiveWorkingDay = prevConsecutiveWorkingDay;
    }

    public long getShortestAndAverageDailyRestInfo() {
        return shortestAndAverageDailyRestInfo;
    }

    public void setShortestAndAverageDailyRestInfo(long shortestAndAverageDailyRestInfo) {
        this.shortestAndAverageDailyRestInfo = shortestAndAverageDailyRestInfo;
    }

    public long getNumberOfWeekendShiftInPeriodInfo() {
        return numberOfWeekendShiftInPeriodInfo;
    }

    public void setNumberOfWeekendShiftInPeriodInfo(long numberOfWeekendShiftInPeriodInfo) {
        this.numberOfWeekendShiftInPeriodInfo = numberOfWeekendShiftInPeriodInfo;
    }

    public long getMaximumShiftsInIntervalInfo() {
        return maximumShiftsInIntervalInfo;
    }

    public void setMaximumShiftsInIntervalInfo(long maximumShiftsInIntervalInfo) {
        this.maximumShiftsInIntervalInfo = maximumShiftsInIntervalInfo;
    }

    public long getMaximumSeniorDaysInYearInfo() {
        return maximumSeniorDaysInYearInfo;
    }

    public void setMaximumSeniorDaysInYearInfo(long maximumSeniorDaysInYearInfo) {
        this.maximumSeniorDaysInYearInfo = maximumSeniorDaysInYearInfo;
    }

    public long getMaximumNumberOfNightsInfo() {
        return maximumNumberOfNightsInfo;
    }

    public void setMaximumNumberOfNightsInfo(long maximumNumberOfNightsInfo) {
        this.maximumNumberOfNightsInfo = maximumNumberOfNightsInfo;
    }

    public long getMaximumDaysOffInPeriodInfo() {
        return maximumDaysOffInPeriodInfo;
    }

    public void setMaximumDaysOffInPeriodInfo(long maximumDaysOffInPeriodInfo) {
        this.maximumDaysOffInPeriodInfo = maximumDaysOffInPeriodInfo;
    }

    public long getMaximumAverageScheduledTimeInfo() {
        return maximumAverageScheduledTimeInfo;
    }

    public void setMaximumAverageScheduledTimeInfo(long maximumAverageScheduledTimeInfo) {
        this.maximumAverageScheduledTimeInfo = maximumAverageScheduledTimeInfo;
    }
}
