package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class NumberOfTaskPerShift extends ConstraintInfo{

    public NumberOfTaskPerShift(int penality,Integer value) {
        super(penality,value);
    }

    public NumberOfTaskPerShift() {
    }
}
