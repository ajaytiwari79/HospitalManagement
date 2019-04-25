package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE3
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumConsecutiveWorkingDaysWTATemplate implements ConstraintHandler {

    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private int daysLimit;//no of days
    private int weight;
    private ScoreLevel level;
    private String templateType;

    public MaximumConsecutiveWorkingDaysWTATemplate(int daysLimit, int weight, ScoreLevel level) {
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

    public long getDaysLimit() {
        return daysLimit;
    }

    public void setDaysLimit(int daysLimit) {
        this.daysLimit = daysLimit;
    }

    public MaximumConsecutiveWorkingDaysWTATemplate(List<String> balanceType, boolean checkAgainstTimeRules, int daysLimit) {
        this.balanceType = balanceType;
        this.checkAgainstTimeRules = checkAgainstTimeRules;
        this.daysLimit = daysLimit;
    }

    public MaximumConsecutiveWorkingDaysWTATemplate() {
    }
    //TODO Test case
    public int getConsecutiveDays(List<LocalDate> localDates) {
        if(localDates.size()<2) return 0;
        Collections.sort(localDates);
        int count = 1;
        int max = 0;
        int l=1;
        while(l<localDates.size()){
            if(localDates.get(l-1).equals(localDates.get(l).minusDays(1))){
                count++;
            }else{
                count=0;
            }
            if(count>max){
                max=count;
            }
            l++;
        }
        return max;
    }

    public int checkConstraints(List<Shift> shifts) {
        int consecutiveDays = getConsecutiveDays(ShiftPlanningUtility.getSortedAndUniqueDates(shifts));
        return consecutiveDays > daysLimit?(consecutiveDays-(int) daysLimit):0;
    }

}
