package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class PlanInsideTimeWindow extends ConstraintInfo{

    public PlanInsideTimeWindow() {
    }

    public PlanInsideTimeWindow(int penality, Integer value) {
        super(penality, value);
    }
}
