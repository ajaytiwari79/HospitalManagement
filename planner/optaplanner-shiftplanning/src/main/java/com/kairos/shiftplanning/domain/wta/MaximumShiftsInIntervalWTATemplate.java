package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import org.joda.time.Interval;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE19
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumShiftsInIntervalWTATemplate implements ConstraintHandler {

    private List<String> balanceType;//multiple check boxes
    private long intervalLength;//
    private String intervalUnit;
    private long validationStartDateMillis;
    private long shiftsLimit;
    private boolean onlyCompositeShifts;//(checkbox)
    private int weight;
    private ScoreLevel level;
    private String templateType;
    //TODO fix needed
    private Interval interval;

    public MaximumShiftsInIntervalWTATemplate(long shiftsLimit, int weight, ScoreLevel level) {
        this.shiftsLimit = shiftsLimit;
        this.weight = weight;
        this.level = level;
    }


    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
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

    public long getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalLength(long intervalLength) {
        this.intervalLength = intervalLength;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public long getShiftsLimit() {
        return shiftsLimit;
    }

    public void setShiftsLimit(long shiftsLimit) {
        this.shiftsLimit = shiftsLimit;
    }

    public boolean isOnlyCompositeShifts() {
        return onlyCompositeShifts;
    }

    public void setOnlyCompositeShifts(boolean onlyCompositeShifts) {
        this.onlyCompositeShifts = onlyCompositeShifts;
    }

    public MaximumShiftsInIntervalWTATemplate(List<String> balanceType, long intervalLength, String intervalUnit, long validationStartDateMillis, long shiftsLimit, boolean onlyCompositeShifts) {
        this.balanceType = balanceType;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.validationStartDateMillis = validationStartDateMillis;
        this.shiftsLimit = shiftsLimit;
        this.onlyCompositeShifts = onlyCompositeShifts;
    }

    public MaximumShiftsInIntervalWTATemplate() {
    }

    public int checkConstraints(List<Shift> shifts){
        int shiftCount = 0;
        for (Shift shift:shifts) {
            if(interval.contains(shift.getStart()))
                shiftCount++;
        }
        return shiftCount>shiftsLimit?(shiftCount-(int)shiftsLimit):0;
    }
}
