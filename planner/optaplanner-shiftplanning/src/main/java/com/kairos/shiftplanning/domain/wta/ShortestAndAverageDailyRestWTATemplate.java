package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE18
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortestAndAverageDailyRestWTATemplate implements ConstraintHandler {


    //Average resting Time in a week

    private List<String> balanceType;//multiple check boxes
    private long intervalLength;//
    private String intervalUnit;
    private long validationStartDateMillis;
    private long continuousDayRestingTime;
    private long averageRest;//(minutes number)
    private String shiftAffiliation;//(List checkbox)
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private Interval interval;


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

    public long getContinuousDayRestingTime() {
        return continuousDayRestingTime;
    }

    public void setContinuousDayRestingTime(long continuousDayRestingTime) {
        this.continuousDayRestingTime = continuousDayRestingTime;
    }

    public long getAverageRest() {
        return averageRest;
    }

    public void setAverageRest(long averageRest) {
        this.averageRest = averageRest;
    }

    public String getShiftAffiliation() {
        return shiftAffiliation;
    }

    public void setShiftAffiliation(String shiftAffiliation) {
        this.shiftAffiliation = shiftAffiliation;
    }


    public ShortestAndAverageDailyRestWTATemplate(long averageRest, int weight, ScoreLevel level) {
        this.averageRest = averageRest;
        this.weight = weight;
        this.level = level;
    }

    public ShortestAndAverageDailyRestWTATemplate(List<String> balanceType, long intervalLength, String intervalUnit, long validationStartDateMillis, long continuousDayRestingTime, long averageRest, String shiftAffiliation) {
        this.balanceType = balanceType;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.validationStartDateMillis = validationStartDateMillis;
        this.continuousDayRestingTime = continuousDayRestingTime;
        this.averageRest = averageRest;
        this.shiftAffiliation = shiftAffiliation;
    }

    public ShortestAndAverageDailyRestWTATemplate() {
    }

    public int checkConstraints(List<Shift> shifts){
        if(shifts.size()<2) return 0;
        List<Interval> intervals= ShiftPlanningUtility.getSortedIntervals(shifts);
        int restingTimeUnder=0;
        int totalRestAllShifts=0;
        for(int i=1;i<intervals.size();i++){
            DateTime lastEnd=intervals.get(i-1).getEnd();
            DateTime thisStart=intervals.get(i).getStart();
            long totalRest=(thisStart.getMillisOfDay()-lastEnd.getMillis())/60000;
            restingTimeUnder=(int)(continuousDayRestingTime >totalRest? continuousDayRestingTime -totalRest:0);
            totalRestAllShifts+=totalRest;
        }
        float averageRestingTime=totalRestAllShifts/shifts.size();
        return  (restingTimeUnder + (int)(averageRest>averageRestingTime?averageRest-averageRestingTime:0));
    }

    public void breakLevelConstraints(HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext,int contraintPenality){
        //Not in use
    }

}
