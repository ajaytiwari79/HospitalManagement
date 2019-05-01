package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.staffing_level.TimeInterval;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.LocalDate;


import java.util.List;
import java.util.Set;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE7
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class MaximumConsecutiveWorkingNightsWTATemplate implements ConstraintHandler,NightWorkTemplate{


    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private long nightsWorked;//no of days
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private long nightStarts;
    private Long nightEnds;


    private TimeInterval nightTimeInterval;

    public MaximumConsecutiveWorkingNightsWTATemplate(long nightsWorked, int weight, ScoreLevel level,long nightStarts,long nightEnds) {
        this.nightsWorked = nightsWorked;
        this.weight = weight;
        this.level = level;
        this.nightStarts = nightStarts;
        this.nightEnds = nightEnds;
        nightTimeInterval =new TimeInterval(nightStarts,nightEnds);
    }

    public long getNightStarts() {
        return nightStarts;
    }

    public void setNightStarts(long nightStarts) {
        this.nightStarts = nightStarts;
    }

    public Long getNightEnds() {
        return nightEnds;
    }

    public void setNightEnds(Long nightEnds) {
        this.nightEnds = nightEnds;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ScoreLevel getLevel() {
        return level;
    }

    public void setLevel(ScoreLevel level) {
        this.level = level;
    }


    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public boolean isCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public void setCheckAgainstTimeRules(boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    @Override
    public TimeInterval getNightTimeInterval() {
        return nightTimeInterval;
    }

    public void setNightTimeInterval(TimeInterval nightTimeInterval) {
        this.nightTimeInterval = nightTimeInterval;
    }

    public MaximumConsecutiveWorkingNightsWTATemplate() {
    }

    private int getConsecutiveNightShifts(Set<LocalDate> localDates, Shift shift){
        int count = 0;
        int i=1;
        LocalDate prevDayOfShift = shift.getStart().toLocalDate().minusDays(i);
        while (true){
            if(localDates.contains(prevDayOfShift)){
                count++;
                i++;
                prevDayOfShift = prevDayOfShift.minusDays(i);
            }else break;
        }
        return count;
    }

    public int checkConstraints(List<Shift> shifts){
        if(shifts.size()<2) return  0;
        int count = 0;
        int consecutiveNightCount = 1;
        ShiftPlanningUtility.sortShifts(shifts);
        List<LocalDate> localDates=ShiftPlanningUtility.getSortedDates(shifts);
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



}
