package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
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
 * TEMPLATE4
 */
@Getter
@Setter
@NoArgsConstructor
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
