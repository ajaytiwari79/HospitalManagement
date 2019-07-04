package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.*;

import java.util.List;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE4
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumRestInConsecutiveDaysWTATemplate implements ConstraintHandler {

    private int minimumRest;//hh:mm
    private int daysWorked;
    private int weight;
    private ScoreLevel level;
    private String templateType;

    public MinimumRestInConsecutiveDaysWTATemplate(int minimumRest,int daysWorked, int weight, ScoreLevel level) {
        this.minimumRest = minimumRest;
        this.weight = weight;
        this.level = level;
        this.daysWorked=daysWorked;
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

    public long getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(int minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(int daysWorked) {
        this.daysWorked = daysWorked;
    }


    public MinimumRestInConsecutiveDaysWTATemplate(int minimumRest, int daysWorked) {
        this.minimumRest = minimumRest;
        this.daysWorked = daysWorked;
    }

    public MinimumRestInConsecutiveDaysWTATemplate() {
    }
    //TODO test case and fix this.
    public int checkConstraints(List<Shift> shifts){
        if(shifts.size()<2) return 0;
        ShiftPlanningUtility.sortShifts(shifts);
        List<LocalDate> dates=ShiftPlanningUtility.getSortedDates(shifts);
        int l=1;
        int consDays=0;
        int totalRestUnder=0;
        while (l<dates.size()){
            if(dates.get(l-1).equals(dates.get(l).minusDays(1))){
                consDays++;
            }else{
                consDays=0;
            }
            if(consDays>=daysWorked){
                DateTime start=shifts.get(l-1).getEnd();
                DateTime end=shifts.get(l).getStart();
                int diff=new Interval(start,end).toDuration().toStandardMinutes().getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }
        return totalRestUnder;
    }

}
