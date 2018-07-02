package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class StartInTimeWindow extends ConstraintInfo{

    public StartInTimeWindow() {
    }

    public StartInTimeWindow(int penality, Integer value) {
        super(penality, value);
    }
}
