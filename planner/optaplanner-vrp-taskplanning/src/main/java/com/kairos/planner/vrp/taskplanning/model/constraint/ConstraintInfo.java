package com.kairos.planner.vrp.taskplanning.model.constraint;

/**
 * @author pradeep
 * @date - 2/7/18
 */

public class ConstraintInfo {

    protected int penality;
    protected Integer value;

    public ConstraintInfo() {
    }

    public ConstraintInfo(int penality, Integer value) {
        this.penality = penality;
        this.value = value;
    }

    public int getPenality() {
        return penality;
    }

    public void setPenality(int penality) {
        this.penality = penality;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
