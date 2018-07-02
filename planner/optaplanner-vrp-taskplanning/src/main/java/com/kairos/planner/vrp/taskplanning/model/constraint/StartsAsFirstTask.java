package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class StartsAsFirstTask extends ConstraintInfo{

    public StartsAsFirstTask() {
    }

    public StartsAsFirstTask(int penality, Integer value) {
        super(penality, value);
    }

}
