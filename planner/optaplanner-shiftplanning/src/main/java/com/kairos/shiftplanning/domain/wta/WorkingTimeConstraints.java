package com.kairos.shiftplanning.domain.wta;

import com.kairos.shiftplanning.domain.shift.Shift;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WorkingTimeConstraints {
    private static Logger log= LoggerFactory.getLogger(WorkingTimeConstraints.class);

    private CareDayCheckWTATemplate careDayCheck;
    private MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTime;
    private MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDays;
    private MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights;
    private MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriod;
    private MaximumNightShiftLengthWTATemplate maximumNightShiftLength;
    private MaximumNumberOfNightsWTATemplate maximumNumberOfNights;
    private MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYear;
    private MaximumShiftLengthWTATemplate maximumShiftLength;
    private MaximumShiftsInIntervalWTATemplate maximumShiftsInInterval;
    private MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriod;
    private MinimumConsecutiveNightsWTATemplate minimumConsecutiveNights;
    private MinimumDailyRestingTimeWTATemplateTemplate minimumDailyRestingTime;
    private MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShift;
    private MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNights;
    private MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDays;
    private MinimumShiftLengthWTATemplate minimumShiftLength;
    private MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriod;
    private NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriod;
    private ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRest;

    public int checkConstraint(Shift shift, int index) {
        //log.debug("checking WTA constraint: {}",index);
        switch (index) {
            case 1:return maximumShiftLength.checkConstraints(shift);
            case 2:return minimumShiftLength.checkConstraints(shift);
            case 5:return maximumNightShiftLength.checkConstraints(shift);
            default:break;
        }
        return 0;
    }

    public int checkConstraint(List<Shift> shifts, int index) {
        //log.debug("checking WTA constraint: {}",index);
        switch (index) {
            case 3:return maximumConsecutiveWorkingDays.checkConstraints(shifts);
            case 4:return minimumRestInConsecutiveDays.checkConstraints(shifts);
            case 6:return minimumConsecutiveNights.checkConstraints(shifts); //Not needed at this instant
            case 7:return maximumConsecutiveWorkingNights.checkConstraints(shifts); //Not needed at this instant
            case 8:return minimumRestConsecutiveNights.checkConstraints(shifts); //Not needed at this instant
            case 9:return maximumNumberOfNights.checkConstraints(shifts); //Not needed at this instant
            //case 10:return maximumDaysOffInPeriod.checkConstraints(shifts);
            case 11:return maximumAverageScheduledTime.checkConstraints(shifts);
            case 13:return numberOfWeekendShiftInPeriod.checkConstraints(shifts);
            case 15:return minimumDailyRestingTime.checkConstraints(shifts);
            case 17:return minimumWeeklyRestPeriod.checkConstraints(shifts);
            case 18:return shortestAndAverageDailyRest.checkConstraints(shifts);
            case 19:return maximumShiftsInInterval.checkConstraints(shifts);
            default:
                break;
        }
        return 0;
    }

    public void breakLevelConstraints(HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext, int index,int contraintPenality) {
        log.debug("breaking WTA constraint: {}",index);
        switch (index) {
            case 1:
                maximumShiftLength.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 2:
                minimumShiftLength.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 3:
                maximumConsecutiveWorkingDays.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 4:
                minimumRestInConsecutiveDays.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 5:
                maximumNightShiftLength.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 6:
                minimumConsecutiveNights.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 7:
                maximumConsecutiveWorkingNights.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 8:
                minimumRestConsecutiveNights.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 9:
                maximumNumberOfNights.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 10:
                maximumDaysOffInPeriod.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 11:
                maximumAverageScheduledTime.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 12:
                maximumVetoPerPeriod.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 13:
                numberOfWeekendShiftInPeriod.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 14:
                careDayCheck.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 15:
                minimumDailyRestingTime.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 16:
                minimumDurationBetweenShift.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 17:
                minimumWeeklyRestPeriod.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 18:
                shortestAndAverageDailyRest.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 19:
                maximumShiftsInInterval.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            case 20:
                maximumSeniorDaysInYear.breakLevelConstraints(scoreHolder, kContext,contraintPenality);
                break;
            default:
                break;
        }
    }

    public CareDayCheckWTATemplate getCareDayCheck() {
        return careDayCheck;
    }

    public void setCareDayCheck(CareDayCheckWTATemplate careDayCheck) {
        this.careDayCheck = careDayCheck;
    }

    public MaximumAverageScheduledTimeWTATemplate getMaximumAverageScheduledTime() {
        return maximumAverageScheduledTime;
    }

    public void setMaximumAverageScheduledTime(MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTime) {
        this.maximumAverageScheduledTime = maximumAverageScheduledTime;
    }

    public MaximumConsecutiveWorkingDaysWTATemplate getMaximumConsecutiveWorkingDays() {
        return maximumConsecutiveWorkingDays;
    }

    public void setMaximumConsecutiveWorkingDays(MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDays) {
        this.maximumConsecutiveWorkingDays = maximumConsecutiveWorkingDays;
    }

    public MaximumConsecutiveWorkingNightsWTATemplate getMaximumConsecutiveWorkingNights() {
        return maximumConsecutiveWorkingNights;
    }

    public void setMaximumConsecutiveWorkingNights(MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights) {
        this.maximumConsecutiveWorkingNights = maximumConsecutiveWorkingNights;
    }

    public MaximumDaysOffInPeriodWTATemplate getMaximumDaysOffInPeriod() {
        return maximumDaysOffInPeriod;
    }

    public void setMaximumDaysOffInPeriod(MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriod) {
        this.maximumDaysOffInPeriod = maximumDaysOffInPeriod;
    }

    public MaximumNightShiftLengthWTATemplate getMaximumNightShiftLength() {
        return maximumNightShiftLength;
    }

    public void setMaximumNightShiftLength(MaximumNightShiftLengthWTATemplate maximumNightShiftLength) {
        this.maximumNightShiftLength = maximumNightShiftLength;
    }

    public MaximumNumberOfNightsWTATemplate getMaximumNumberOfNights() {
        return maximumNumberOfNights;
    }

    public void setMaximumNumberOfNights(MaximumNumberOfNightsWTATemplate maximumNumberOfNights) {
        this.maximumNumberOfNights = maximumNumberOfNights;
    }

    public MaximumSeniorDaysInYearWTATemplate getMaximumSeniorDaysInYear() {
        return maximumSeniorDaysInYear;
    }

    public void setMaximumSeniorDaysInYear(MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYear) {
        this.maximumSeniorDaysInYear = maximumSeniorDaysInYear;
    }

    public MaximumShiftLengthWTATemplate getMaximumShiftLength() {
        return maximumShiftLength;
    }

    public void setMaximumShiftLength(MaximumShiftLengthWTATemplate maximumShiftLength) {
        this.maximumShiftLength = maximumShiftLength;
    }

    public MaximumShiftsInIntervalWTATemplate getMaximumShiftsInInterval() {
        return maximumShiftsInInterval;
    }

    public void setMaximumShiftsInInterval(MaximumShiftsInIntervalWTATemplate maximumShiftsInInterval) {
        this.maximumShiftsInInterval = maximumShiftsInInterval;
    }

    public MaximumVetoPerPeriodWTATemplate getMaximumVetoPerPeriod() {
        return maximumVetoPerPeriod;
    }

    public void setMaximumVetoPerPeriod(MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriod) {
        this.maximumVetoPerPeriod = maximumVetoPerPeriod;
    }

    public MinimumConsecutiveNightsWTATemplate getMinimumConsecutiveNights() {
        return minimumConsecutiveNights;
    }

    public void setMinimumConsecutiveNights(MinimumConsecutiveNightsWTATemplate minimumConsecutiveNights) {
        this.minimumConsecutiveNights = minimumConsecutiveNights;
    }

    public MinimumDailyRestingTimeWTATemplateTemplate getMinimumDailyRestingTime() {
        return minimumDailyRestingTime;
    }

    public void setMinimumDailyRestingTime(MinimumDailyRestingTimeWTATemplateTemplate minimumDailyRestingTime) {
        this.minimumDailyRestingTime = minimumDailyRestingTime;
    }

    public MinimumDurationBetweenShiftWTATemplate getMinimumDurationBetweenShift() {
        return minimumDurationBetweenShift;
    }

    public void setMinimumDurationBetweenShift(MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShift) {
        this.minimumDurationBetweenShift = minimumDurationBetweenShift;
    }

    public MinimumRestConsecutiveNightsWTATemplate getMinimumRestConsecutiveNights() {
        return minimumRestConsecutiveNights;
    }

    public void setMinimumRestConsecutiveNights(MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNights) {
        this.minimumRestConsecutiveNights = minimumRestConsecutiveNights;
    }

    public MinimumRestInConsecutiveDaysWTATemplate getMinimumRestInConsecutiveDays() {
        return minimumRestInConsecutiveDays;
    }

    public void setMinimumRestInConsecutiveDays(MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDays) {
        this.minimumRestInConsecutiveDays = minimumRestInConsecutiveDays;
    }

    public MinimumShiftLengthWTATemplate getMinimumShiftLength() {
        return minimumShiftLength;
    }

    public void setMinimumShiftLength(MinimumShiftLengthWTATemplate minimumShiftLength) {
        this.minimumShiftLength = minimumShiftLength;
    }

    public MinimumWeeklyRestPeriodWTATemplate getMinimumWeeklyRestPeriod() {
        return minimumWeeklyRestPeriod;
    }

    public void setMinimumWeeklyRestPeriod(MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriod) {
        this.minimumWeeklyRestPeriod = minimumWeeklyRestPeriod;
    }

    public NumberOfWeekendShiftInPeriodWTATemplate getNumberOfWeekendShiftInPeriod() {
        return numberOfWeekendShiftInPeriod;
    }

    public void setNumberOfWeekendShiftInPeriod(NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriod) {
        this.numberOfWeekendShiftInPeriod = numberOfWeekendShiftInPeriod;
    }

    public ShortestAndAverageDailyRestWTATemplate getShortestAndAverageDailyRest() {
        return shortestAndAverageDailyRest;
    }

    public void setShortestAndAverageDailyRest(ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRest) {
        this.shortestAndAverageDailyRest = shortestAndAverageDailyRest;
    }
}
