package com.kairos.persistence.model.solver_config;

import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.enums.constraint.ConstraintCategory;
import com.kairos.enums.solver_config.PlanningType;

/**
 * @author pradeep
 * @date - 20/6/18
 */

public class Constraint extends MongoBaseEntity {

    private String name;
    private String description;
    private ConstraintCategory category;
    private PlanningType planningType;
    private Long unitId;
    private boolean constraintValueRequired;
    private boolean penalityValueRequired;
    private boolean disabled;


    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isConstraintValueRequired() {
        return constraintValueRequired;
    }

    public void setConstraintValueRequired(boolean constraintValueRequired) {
        this.constraintValueRequired = constraintValueRequired;
    }

    public boolean isPenalityValueRequired() {
        return penalityValueRequired;
    }

    public void setPenalityValueRequired(boolean penalityValueRequired) {
        this.penalityValueRequired = penalityValueRequired;
    }

    public PlanningType getPlanningType() {
        return planningType;
    }

    public void setPlanningType(PlanningType planningType) {
        this.planningType = planningType;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Constraint() {
    }

    public Constraint(String name, String description, ConstraintCategory category,PlanningType planningType,Long unitId,boolean constraintValueRequired,boolean penalityValueRequired,boolean disabled) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.planningType = planningType;
        this.unitId = unitId;
        this.constraintValueRequired = constraintValueRequired;
        this.penalityValueRequired=penalityValueRequired;
        this.disabled = disabled;
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

    public ConstraintCategory getCategory() {
        return category;
    }

    public void setCategory(ConstraintCategory category) {
        this.category = category;
    }


}
