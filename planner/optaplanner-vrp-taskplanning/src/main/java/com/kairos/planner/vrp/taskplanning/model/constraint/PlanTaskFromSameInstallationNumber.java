package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class PlanTaskFromSameInstallationNumber extends ConstraintInfo{

    public PlanTaskFromSameInstallationNumber() {
    }

    public PlanTaskFromSameInstallationNumber(int penality, Integer value) {
        super(penality, value);
    }
}
