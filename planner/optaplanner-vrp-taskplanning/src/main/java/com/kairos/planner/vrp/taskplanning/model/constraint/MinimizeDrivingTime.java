package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class MinimizeDrivingTime extends ConstraintInfo{

    public MinimizeDrivingTime(int penality, Integer value) {
        super(penality, value);
    }

    public MinimizeDrivingTime() {
    }
}
