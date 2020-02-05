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
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumWeeklyRestPeriodWTATemplate implements ConstraintHandler {

    private long continuousWeekRest;
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private Interval interval;

    public MinimumWeeklyRestPeriodWTATemplate(long continuousWeekRest, int weight, ScoreLevel level) {
        this.continuousWeekRest = continuousWeekRest;
        this.weight = weight;
        this.level = level;
    }

    public int checkConstraints(List<Shift> shifts){
        if(shifts.size()<2) return 0;
        int totalRestTime = interval.toPeriod().getMinutes();
        for (Shift shift:shifts) {
            totalRestTime-=shift.getMinutes();
        }
        return totalRestTime<continuousWeekRest?(totalRestTime-(int)continuousWeekRest):0;
    }
}
