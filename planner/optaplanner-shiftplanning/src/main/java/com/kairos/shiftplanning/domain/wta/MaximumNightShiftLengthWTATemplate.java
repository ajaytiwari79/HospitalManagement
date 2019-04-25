package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staffing_level.TimeInterval;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE5
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumNightShiftLengthWTATemplate implements ConstraintHandler,NightWorkTemplate {

    private int timeLimit;
    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private long nightStarts;
    private long nightEnds;
    private TimeInterval timeInterval;

    public MaximumNightShiftLengthWTATemplate(int timeLimit, int weight, ScoreLevel level,long nightStarts,long nightEnds) {
        this.timeLimit = timeLimit;
        this.weight = weight;
        this.level = level;
        this.nightStarts = nightStarts;
        this.nightEnds = nightEnds;
        timeInterval=new TimeInterval(nightStarts,nightEnds);
    }

    public long getNightStarts() {
        return nightStarts;
    }

    public void setNightStarts(long nightStarts) {
        this.nightStarts = nightStarts;
    }

    public long getNightEnds() {
        return nightEnds;
    }

    public void setNightEnds(long nightEnds) {
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

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
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


    public MaximumNightShiftLengthWTATemplate() {
    }

    public int checkConstraints(Shift shift){
        if(isNightShift(shift)){
            return !((ShiftImp)shift).isAbsenceActivityApplied() && shift.getMinutes() > timeLimit?(shift.getMinutes()-timeLimit):0;
        }
        return 0;
    }

    /*public void breakLevelConstraints(HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext,int contraintPenality){
        switch (level){
            case "Hard":scoreHolder.addHardConstraintMatch(kContext,weight*contraintPenality);
                break;
            case "Medium":scoreHolder.addMediumConstraintMatch(kContext,weight*contraintPenality);
                break;
            case "Soft":scoreHolder.addSoftConstraintMatch(kContext,weight*contraintPenality);
                break;
        }
    }*/

    @Override
    public TimeInterval getNightTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }
}