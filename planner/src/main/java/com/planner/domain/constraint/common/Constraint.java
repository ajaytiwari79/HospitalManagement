package com.planner.domain.constraint.common;

import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.enums.constraint.*;
import com.planner.domain.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection="constraint")
public class Constraint extends MongoBaseEntity{
    protected String name;
    protected String description;
    protected ConstraintType constraintType;
    protected ConstraintSubType constraintSubType;
    protected ConstraintLevel constraintLevel;
    protected int penalty;
    protected BigInteger planningProblemId;
    protected BigInteger parentConstraintId;//copiedFrom


    public Constraint(){
        //Default Constructor
    }

    public Constraint(BigInteger id, String name, String description, ConstraintType constraintType, ConstraintSubType constraintSubType, ConstraintLevel constraintLevel, int penalty, BigInteger planningProblemId, BigInteger parentConstraintId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.constraintType = constraintType;
        this.constraintSubType = constraintSubType;
        this.constraintLevel = constraintLevel;
        this.penalty = penalty;
        this.planningProblemId = planningProblemId;
        this.parentConstraintId = parentConstraintId;
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

    public BigInteger getPlanningProblemId() {
        return planningProblemId;
    }

    public void setPlanningProblemId(BigInteger planningProblemId) {
        this.planningProblemId = planningProblemId;
    }

    public BigInteger getParentConstraintId() {
        return parentConstraintId;
    }

    public void setParentConstraintId(BigInteger parentConstraintId) {
        this.parentConstraintId = parentConstraintId;
    }

    public boolean isEqualsWithSpecificField(ConstraintDTO constraintDTO) {
        return constraintType.equals(constraintDTO.getConstraintType()) && constraintSubType.equals(constraintDTO.getConstraintSubType()) && constraintLevel.equals(constraintDTO.getConstraintLevel()) && penalty==penalty;
    }


}
