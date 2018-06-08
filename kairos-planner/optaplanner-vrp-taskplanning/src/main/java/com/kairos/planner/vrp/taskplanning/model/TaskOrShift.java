package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public class TaskOrShift {

    @InverseRelationShadowVariable(sourceVariableName = "prevTaskOrShift")
    private Task nextTask;
}
