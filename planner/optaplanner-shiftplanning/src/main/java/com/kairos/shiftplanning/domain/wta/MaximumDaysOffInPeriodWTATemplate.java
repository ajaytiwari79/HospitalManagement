package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE10
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumDaysOffInPeriodWTATemplate implements ConstraintHandler {

    private List<String> balanceType;//multiple check boxes
    private long intervalLength;
    private String intervalUnit;
    private long validationStartDateMillis;
    private int daysLimit;
    private int weight;
    private ScoreLevel level;
    private String templateType;

    public int checkConstraints(List<Shift> shifts){
        int shiftsNum=ShiftPlanningUtility.getSortedDates(shifts).size();
        return 7-shiftsNum>daysLimit?0:(daysLimit-(7 - shiftsNum));
    }
}
