package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class TaskFromSameInstallationNumber extends ConstraintInfo{

    public TaskFromSameInstallationNumber() {
    }

    public TaskFromSameInstallationNumber(int penality, Integer value) {
        super(penality, value);
    }
}
