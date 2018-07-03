package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class OptimizePlanBasedOnSkill extends ConstraintInfo{

    public OptimizePlanBasedOnSkill() {
    }

    public OptimizePlanBasedOnSkill(int penality, Integer value) {
        super(penality, value);
    }
}
