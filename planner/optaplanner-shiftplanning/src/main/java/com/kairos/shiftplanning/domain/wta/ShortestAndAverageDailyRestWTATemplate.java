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
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE18
 */
@Getter
@Setter
@NoArgsConstructor
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


    public ShortestAndAverageDailyRestWTATemplate(long averageRest, int weight, ScoreLevel level) {
        this.averageRest = averageRest;
        this.weight = weight;
        this.level = level;
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
