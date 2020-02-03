package com.kairos.shiftplanning.domain.wta;

import com.kairos.shiftplanning.domain.shift.Shift;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
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

}
