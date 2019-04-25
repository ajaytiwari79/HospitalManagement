package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE10
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumDaysOffInPeriodWTATemplate implements ConstraintHandler {

    private List<String> balanceType;//multiple check boxes
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private int daysLimit;
    private int weight;
    private ScoreLevel level;
    private String templateType;

    public MaximumDaysOffInPeriodWTATemplate(int daysLimit, int weight, ScoreLevel level) {
        this.daysLimit = daysLimit;
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


    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(int daysLimit) {
        this.daysLimit = daysLimit;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }


    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public MaximumDaysOffInPeriodWTATemplate(List<String> balanceType, long intervalLength, String intervalUnit, long validationStartDateMillis, int daysLimit) {
        this.balanceType = balanceType;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.validationStartDateMillis = validationStartDateMillis;
        this.daysLimit = daysLimit;
    }

    public MaximumDaysOffInPeriodWTATemplate() {
    }

    public int checkConstraints(List<Shift> shifts){
        int shiftsNum=ShiftPlanningUtility.getSortedDates(shifts).size();
        return 7-shiftsNum>daysLimit?0:(daysLimit-(7 - shiftsNum));
    }
}
