package com.planner.domain.constraint.common;

import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ConstraintLevel;
import com.planner.domain.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection="constraint")
public class Constraint extends MongoBaseEntity{

    //~
    protected BigInteger id;
    protected String name;
    protected String description;
    protected ConstraintType constraintType;
    protected ConstraintSubType constraintSubType;
    protected ConstraintLevel constraintLevel;
    protected int penalty;
    protected Long planningProblemId;
    protected BigInteger parentCountryConstraintId;//copiedFrom

    //=================================================
    public Constraint(){}

    public Constraint(BigInteger id, String name, String description, ConstraintType constraintType, ConstraintSubType constraintSubType, ConstraintLevel constraintLevel, int penalty, Long planningProblemId, BigInteger parentCountryConstraintId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.constraintType = constraintType;
        this.constraintSubType = constraintSubType;
        this.constraintLevel = constraintLevel;
        this.penalty = penalty;
        this.planningProblemId = planningProblemId;
        this.parentCountryConstraintId = parentCountryConstraintId;
    }

    // =================================================
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConstraintType getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(ConstraintType constraintType) {
        this.constraintType = constraintType;
    }

    public ConstraintSubType getConstraintSubType() {
        return constraintSubType;
    }

    public void setConstraintSubType(ConstraintSubType constraintSubType) {
        this.constraintSubType = constraintSubType;
    }

    public ConstraintLevel getConstraintLevel() {
        return constraintLevel;
    }

    public void setConstraintLevel(ConstraintLevel constraintLevel) {
        this.constraintLevel = constraintLevel;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public Long getPlanningProblemId() {
        return planningProblemId;
    }

    public void setPlanningProblemId(Long planningProblemId) {
        this.planningProblemId = planningProblemId;
    }

    public BigInteger getParentCountryConstraintId() {
        return parentCountryConstraintId;
    }

    public void setParentCountryConstraintId(BigInteger parentCountryConstraintId) {
        this.parentCountryConstraintId = parentCountryConstraintId;
    }
}
