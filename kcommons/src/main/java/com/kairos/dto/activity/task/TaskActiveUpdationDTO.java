package com.kairos.dto.activity.task;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by neuron on 23/5/17.
 */
public class TaskActiveUpdationDTO {

    public void setTaskIds(List<BigInteger> taskIds) {
        this.taskIds = taskIds;
    }

    public void setMakeActive(boolean makeActive) {
        this.makeActive = makeActive;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public TaskActiveUpdationDTO(){

    }

    public TaskActiveUpdationDTO(List<BigInteger> taskIds, boolean makeActive, int unitId) {
        this.taskIds = taskIds;
        this.makeActive = makeActive;
        this.unitId = unitId;
    }

    List<BigInteger> taskIds;
    boolean makeActive;
    int unitId;

    public boolean isMakeActive() {
        return makeActive;
    }

    public int getUnitId() {
        return unitId;
    }

    public List<BigInteger> getTaskIds() {
        return taskIds;
    }
}
