package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class MustBePlanned extends ConstraintInfo{

    public MustBePlanned() {
    }

    public MustBePlanned(int penality, Integer value) {
        super(penality, value);
    }
}
