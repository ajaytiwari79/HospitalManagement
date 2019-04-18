package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.constraints.ScoreLevel;


import java.util.List;

/**
 * Created by Pradeep singh on 4/8/17.
 * TEMPLATE1
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumShiftLengthWTATemplate implements ConstraintHandler {
    // In minutes
    private long timeLimit;
    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private int weight;
    private ScoreLevel level;
    private String templateType;

    public MaximumShiftLengthWTATemplate(long timeLimit, int weight, ScoreLevel level) {
        this.timeLimit = timeLimit;
        this.weight = weight;
        this.level = level;
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


    public MaximumShiftLengthWTATemplate() {

    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
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

    public int checkConstraints(Shift shift){
        return !((ShiftImp)shift).isAbsenceActivityApplied()&& shift.getMinutes()>timeLimit?(shift.getMinutes()-(int)timeLimit):0;
    }


}
