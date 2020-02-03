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
import java.util.Set;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE7
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class MaximumConsecutiveWorkingNightsWTATemplate implements ConstraintHandler,NightWorkTemplate{


    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private long nightsWorked;//no of days
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private long nightStarts;
    private Long nightEnds;


    private TimeInterval nightTimeInterval;

    public MaximumConsecutiveWorkingNightsWTATemplate(long nightsWorked, int weight, ScoreLevel level,long nightStarts,long nightEnds) {
        this.nightsWorked = nightsWorked;
        this.weight = weight;
        this.level = level;
        this.nightStarts = nightStarts;
        this.nightEnds = nightEnds;
        nightTimeInterval =new TimeInterval(nightStarts,nightEnds);
    }

    @Override
    public TimeInterval getNightTimeInterval() {
        return nightTimeInterval;
    }


    private int getConsecutiveNightShifts(Set<LocalDate> localDates, Shift shift){
        int count = 0;
        int i=1;
        LocalDate prevDayOfShift = shift.getStart().toLocalDate().minusDays(i);
        while (true){
            if(localDates.contains(prevDayOfShift)){
                count++;
                i++;
                prevDayOfShift = prevDayOfShift.minusDays(i);
            }else break;
        }
        return count;
    }

    public int checkConstraints(List<Shift> shifts){
        if(shifts.size()<2) return  0;
        int count = 0;
        int consecutiveNightCount = 1;
        ShiftPlanningUtility.sortShifts(shifts);
        List<LocalDate> localDates=ShiftPlanningUtility.getSortedDates(shifts);
        for (int i=localDates.size()-1;i>=0;i--){
            if(i!=0){
                if(localDates.get(i-1).equals(localDates.get(i).minusDays(1))  && isNightShift(shifts.get(i))&& isNightShift(shifts.get(i-1))){
                    count++;
                }else {
                    count = 0;
                }
            }
            if(consecutiveNightCount<count){
                consecutiveNightCount = count;
            }
        }
        return consecutiveNightCount > nightsWorked?(consecutiveNightCount-(int) nightsWorked):0;
    }



}
