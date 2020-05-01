package com.kairos.dto.planner.constarints;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class ConstraintDTO {
    //~
    protected BigInteger id;
    protected String name;
    protected String description;
    protected ConstraintType constraintType;
    protected ConstraintSubType constraintSubType;
    protected ScoreLevel scoreLevel;
    protected int penalty;
    protected Long planningProblemId;


    public ConstraintDTO(String name, String description, ConstraintType constraintType, ConstraintSubType constraintSubType, ScoreLevel scoreLevel, int penalty, Long planningProblemId) {
        this.name = name;
        this.description = description;
        this.constraintType = constraintType;
        this.constraintSubType = constraintSubType;
        this.scoreLevel = scoreLevel;
        this.penalty = penalty;
        this.planningProblemId = planningProblemId;
    }



}
