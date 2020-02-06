package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.staffing_level.TimeInterval;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE8
 */
@Getter
@Setter
@NoArgsConstructor
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
