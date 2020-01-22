package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.Interval;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE19
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class    MaximumShiftsInIntervalWTATemplate implements ConstraintHandler {

    private List<String> balanceType;//multiple check boxes
    private long intervalLength;//
    private String intervalUnit;
    private long validationStartDateMillis;
    private long shiftsLimit;
    private boolean onlyCompositeShifts;//(checkbox)
    private int weight;
    private ScoreLevel level;
    private String templateType;
    //TODO fix needed
    private Interval interval;

    public MaximumShiftsInIntervalWTATemplate(long shiftsLimit, int weight, ScoreLevel level) {
        this.shiftsLimit = shiftsLimit;
        this.weight = weight;
        this.level = level;
    }

    public int checkConstraints(List<Shift> shifts){
        int shiftCount = 0;
        for (Shift shift:shifts) {
            if(interval.contains(shift.getStart()))
                shiftCount++;
        }
        return shiftCount>shiftsLimit?(shiftCount-(int)shiftsLimit):0;
    }
}
