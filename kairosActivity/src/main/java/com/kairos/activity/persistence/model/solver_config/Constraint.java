package com.kairos.activity.persistence.model.solver_config;

import com.kairos.activity.persistence.model.activity.PlannedTimeType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.enums.solver_config.ConstraintCategory;
import com.kairos.enums.solver_config.PlanningType;

import java.util.List;

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

    public Constraint(String name, String description, ConstraintCategory category,PlanningType planningType,Long unitId) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.planningType = planningType;
        this.unitId = unitId;
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
