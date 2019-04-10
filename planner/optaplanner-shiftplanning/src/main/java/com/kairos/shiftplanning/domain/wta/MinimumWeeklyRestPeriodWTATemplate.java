package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import org.joda.time.Interval;

import java.util.List;


/**
 * Created by Pradeep singh on 5/8/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumWeeklyRestPeriodWTATemplate implements ConstraintHandler {

    private long continuousWeekRest;
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private Interval interval;

    public MinimumWeeklyRestPeriodWTATemplate(long continuousWeekRest, int weight, ScoreLevel level) {
        this.continuousWeekRest = continuousWeekRest;
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

    public long getContinuousWeekRest() {
        return continuousWeekRest;
    }

    public void setContinuousWeekRest(long continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public MinimumWeeklyRestPeriodWTATemplate(long continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public MinimumWeeklyRestPeriodWTATemplate() {
    }


   /* public boolean checkConsTraints(List<Shift> shifts, Shift shift){
        boolean isValid = false;
        DateTime startOFWeek = shift.getStart().withDayOfWeek(DateTimeConstants.MONDAY).withTimeAtStartOfDay();
        DateTime endOFWeek = shift.getStart().plusWeeks(1).withDayOfWeek(DateTimeConstants.SUNDAY).withTimeAtStartOfDay();
        Interval currentWeekInterval = new Interval(startOFWeek,endOFWeek);
        int totalRestTime = currentWeekInterval.toPeriod().getMinutes();
        for (Shift shift1:shifts) {
            if(shift1.getStart()!=null && shift1.getEnd()!=null && currentWeekInterval.contains(shift.getStart()) && currentWeekInterval.contains(shift.getEnd())){
                totalRestTime-=new Period(shift.getStart(),shift.getEnd()).getMinutes();
            }
            else {
                if(currentWeekInterval.contains(shift.getStart()) && !currentWeekInterval.contains(shift.getEnd())){
                    totalRestTime -= new Period(shift.getStart(),currentWeekInterval.getEnd()).getMinutes();
                }else if(!currentWeekInterval.contains(shift.getStart()) && currentWeekInterval.contains(shift.getEnd())){
                    totalRestTime -= new Period(currentWeekInterval.getStart(),shift.getEnd()).getMinutes();
                }
            }
        }
        if(totalRestTime<continuousWeekRest){
            isValid = true;
        }
        return false;
    }*/

    public int checkConstraints(List<Shift> shifts){
        if(shifts.size()<2) return 0;
        int totalRestTime = interval.toPeriod().getMinutes();
        for (Shift shift:shifts) {
            totalRestTime-=shift.getMinutes();
        }
        return totalRestTime<continuousWeekRest?(totalRestTime-(int)continuousWeekRest):0;
    }
}
