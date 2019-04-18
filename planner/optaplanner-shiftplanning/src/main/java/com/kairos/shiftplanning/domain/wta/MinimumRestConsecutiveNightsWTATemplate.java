package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.staffing_level.TimeInterval;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;


import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE8
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumRestConsecutiveNightsWTATemplate implements ConstraintHandler,NightWorkTemplate{


    private List<String> balanceType;//multiple check boxes
    private int minimumRest;
    private int nightsWorked;
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private long nightStarts;
    private long nightEnds;
    private TimeInterval nightTimeInterval;



    public MinimumRestConsecutiveNightsWTATemplate(int minimumRest,int nightsWorked, int weight, ScoreLevel level,long nightStarts,long nightEnds) {
        this.nightsWorked=nightsWorked;
        this.minimumRest = minimumRest;
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

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public long getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(int minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(int nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public MinimumRestConsecutiveNightsWTATemplate() {
    }

    public int checkConstraints(List<Shift> shifts) {
        if(shifts.size()<2) return 0;
        ShiftPlanningUtility.sortShifts(shifts);
        List<LocalDate> dates=ShiftPlanningUtility.getSortedDates(shifts);
        int l=1;
        int consDays=0;
        int totalRestUnder=0;
        while (l<dates.size()){
            if(dates.get(l-1).equals(dates.get(l).minusDays(1)) && isNightShift(shifts.get(l))&& isNightShift(shifts.get(l-1))){
                consDays++;
            }else{
                consDays=0;
            }
            if(consDays>=nightsWorked){
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

    @Override
    public TimeInterval getNightTimeInterval() {
        return nightTimeInterval;
    }

    public void setNightTimeInterval(TimeInterval nightTimeInterval) {
        this.nightTimeInterval = nightTimeInterval;
    }
}
