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

import java.util.List;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE15
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumDailyRestingTimeWTATemplateTemplate implements ConstraintHandler {

    //Total Resting Time in a Day

    private long dailyRestingTime;
    private int weight;
    private ScoreLevel level;
    private String templateType;


    public MinimumDailyRestingTimeWTATemplateTemplate(long dailyRestingTime, int weight, ScoreLevel level) {
        this.dailyRestingTime = dailyRestingTime;
        this.weight = weight;
        this.level = level;
    }

    public int checkConstraints(List<Shift> shifts){
        if(shifts.size()<2) return 0;
        List<Interval> intervals=ShiftPlanningUtility.getSortedIntervals(shifts);
        int restingTimeUnder=0;
        for(int i=1;i<intervals.size();i++){
            DateTime lastEnd=intervals.get(i-1).getEnd();
            DateTime thisStart=intervals.get(i).getStart();
            long totalRest=(thisStart.getMillis()-lastEnd.getMillis())/60000;
            restingTimeUnder=(int)(dailyRestingTime >totalRest? dailyRestingTime -totalRest:0);//TODO do we need to verify if shifts overlap. Not needed but possible while it'
        }

        return restingTimeUnder;
    }

}