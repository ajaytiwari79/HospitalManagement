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
import org.joda.time.LocalDate;

import java.util.List;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE6
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumConsecutiveNightsWTATemplate implements ConstraintHandler, NightWorkTemplate {


    private int daysLimit;
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private long nightStarts;
    private long nightEnds;

    @Override
    public TimeInterval getNightTimeInterval() {
        return nightTimeInterval;
    }


    private TimeInterval nightTimeInterval;

    public MinimumConsecutiveNightsWTATemplate(int daysLimit, int weight, ScoreLevel level,long nightStarts,long nightEnds) {
        this.daysLimit = daysLimit;
        this.weight = weight;
        this.level = level;
        this.nightStarts = nightStarts;
        this.nightEnds = nightEnds;
        nightTimeInterval =new TimeInterval(nightStarts,nightEnds);
    }

    public int checkConstraints(List<Shift> shifts){
        if(shifts.size()<2) return 0;
        int count = 0;
        int consecutiveNightCount=1;
        ShiftPlanningUtility.sortShifts(shifts);
        List<LocalDate> localDates= ShiftPlanningUtility.getSortedDates(shifts);

        for (int i=localDates.size()-1;i>=0;i--){
            if(i!=0){
                if(localDates.get(i-1).equals(localDates.get(i).minusDays(1)) && isNightShift(shifts.get(i)) &&  isNightShift(shifts.get(i-1))){
                    count++;
                }else {
                    count = 0;
                }
            }
            if(consecutiveNightCount<count){
                consecutiveNightCount = count;
            }
        }
        return consecutiveNightCount < daysLimit?(daysLimit - consecutiveNightCount):0;
    }
}
