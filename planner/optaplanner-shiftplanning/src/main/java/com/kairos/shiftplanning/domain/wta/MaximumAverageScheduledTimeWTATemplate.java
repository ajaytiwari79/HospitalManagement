package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.IntervalUnit;
import com.kairos.shiftplanning.domain.Shift;
import com.kairos.shiftplanning.domain.constraints.ScoreLevel;
import com.kairos.shiftplanning.utils.JodaIntervalConverter;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.joda.time.Interval;
import org.joda.time.LocalDate;


import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumAverageScheduledTimeWTATemplate implements ConstraintHandler {

    //Total Average Working Time in a period

    private List<String> balanceType;//multiple check boxes
    private int intervalLength;
    private IntervalUnit intervalUnit;
    private long validationStartDateMillis;
    private boolean balanceAdjustment;
    private boolean useShiftTimes;
    private long maximumAvgTime;
    private int weight;
    private ScoreLevel level;
    private String templateType;
    @XStreamConverter(JodaIntervalConverter.class)
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

    public void setIntervalLength(int intervalLength) {
        this.intervalLength = intervalLength;
    }

    public long getValidationStartDateMillis() {
        return validationStartDateMillis;
    }


    public boolean isBalanceAdjustment() {
        return balanceAdjustment;
    }

    public void setBalanceAdjustment(boolean balanceAdjustment) {
        this.balanceAdjustment = balanceAdjustment;
    }

    public void setValidationStartDateMillis(long validationStartDateMillis) {
        this.validationStartDateMillis = validationStartDateMillis;
    }

    public boolean isUseShiftTimes() {
        return useShiftTimes;
    }

    public void setUseShiftTimes(boolean useShiftTimes) {
        this.useShiftTimes = useShiftTimes;
    }
    public MaximumAverageScheduledTimeWTATemplate() {
    }
    public MaximumAverageScheduledTimeWTATemplate(List<String> balanceType, int intervalLength, IntervalUnit intervalUnit, long validationStartDateMillis, boolean balanceAdjustment, boolean useShiftTimes, long maximumAvgTime) {
        this.balanceType = balanceType;
        this.intervalLength = intervalLength;
        this.intervalUnit = intervalUnit;
        this.validationStartDateMillis = validationStartDateMillis;
        this.balanceAdjustment = balanceAdjustment;
        this.useShiftTimes = useShiftTimes;
        this.maximumAvgTime = maximumAvgTime;
    }

    public MaximumAverageScheduledTimeWTATemplate(long maximumAvgTime, int intervalLength, int weight, ScoreLevel level, LocalDate weekStart){
        this.weight = weight;
        this.maximumAvgTime = maximumAvgTime;
        this.level = level;
        intervalUnit=IntervalUnit.WEEKS;
        this.intervalLength=intervalLength;
        this.interval=initializeInterval(weekStart);
    }

    private Interval initializeInterval(LocalDate weekStart) {
        return ShiftPlanningUtility.createInterval(weekStart,intervalLength,intervalUnit);
    }

    public IntervalUnit getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(IntervalUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public long getMaximumAvgTime() {
        return maximumAvgTime;
    }

    public void setMaximumAvgTime(long maximumAvgTime) {
        this.maximumAvgTime = maximumAvgTime;
    }


    public int checkConstraints(List<Shift> shifts){
        int totalScheduledTime = 0;//(int) shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumAverageScheduledTimeInfo();
        for (Shift shift:shifts) {
            if(interval.overlaps(shift.getInterval())){
                totalScheduledTime+=interval.overlap(shift.getInterval()).toPeriod().getMinutes();
            }
        }
        return totalScheduledTime>maximumAvgTime?totalScheduledTime-(int)maximumAvgTime:0;
    }
}
