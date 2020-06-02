package com.kairos.dto.planner.solverconfig;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
public class ConstraintDTO {

    private String name;
    private String descriptions;
    private ConstraintSubType constraintSubType;
    private Boolean  mandatory;
    private ScoreLevel scoreLevel;
    private int constraintWeight;
    private ConstraintType constraintType;

    public ConstraintDTO( ConstraintType constraintType,ConstraintSubType constraintSubType, ScoreLevel scoreLevel, int constraintWeight) {
        this.constraintSubType = constraintSubType;
        this.scoreLevel = scoreLevel;
        this.constraintWeight = constraintWeight;
        this.constraintType = constraintType;
    }
}
