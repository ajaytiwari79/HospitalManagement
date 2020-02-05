package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE2
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinimumShiftLengthWTATemplate implements ConstraintHandler {

    private long timeLimit;
    private List<String> balanceType;//multiple check boxes
    private boolean checkAgainstTimeRules;
    private int weight;
    private ScoreLevel level;
    private String templateType;


    public MinimumShiftLengthWTATemplate(long timeLimit, int weight, ScoreLevel level) {
        this.timeLimit = timeLimit;
        this.weight = weight;
        this.level = level;
    }


    public int checkConstraints(Shift shift){
        return !((ShiftImp)shift).isAbsenceActivityApplied() && shift.getMinutes()<timeLimit?((int) timeLimit-shift.getMinutes()):0;
    }
}