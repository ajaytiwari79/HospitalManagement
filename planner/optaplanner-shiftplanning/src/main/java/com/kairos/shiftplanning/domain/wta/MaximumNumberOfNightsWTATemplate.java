package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.staffing_level.TimeInterval;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE9
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumNumberOfNightsWTATemplate implements ConstraintHandler,NightWorkTemplate {

    private List<String> balanceType;//multiple check boxes
    private int nightsWorked;
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private TimeInterval nightTimeInterval;
    private long nightStarts;
    private long nightEnds;

    public MaximumNumberOfNightsWTATemplate(int nightsWorked, int weight, ScoreLevel level,long nightStarts,long nightEnds) {
        this.nightsWorked = nightsWorked;
        this.weight = weight;
        this.level = level;
        this.nightStarts = nightStarts;
        this.nightEnds = nightEnds;
        nightTimeInterval =new TimeInterval(nightStarts,nightEnds);
    }

    public int checkConstraints(List<Shift> shifts){
        if(shifts.size()<0) return 0;
        int count = (int)shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumNumberOfNightsInfo();
        for (Shift shift:shifts){
            if(isNightShift(shift)){
                count++;
            }
        }
        return count > nightsWorked?(count- nightsWorked):0;
    }
    @Override
    public TimeInterval getNightTimeInterval() {
        return nightTimeInterval;
    }

    public void setNightTimeInterval(TimeInterval nightTimeInterval) {
        this.nightTimeInterval = nightTimeInterval;
    }
}
