package com.kairos.wrappers;

import java.util.List;

/**
 * Created by prabjot on 11/11/17.
 */
public class TaskCountWithAssignedUnit {

    private Long totalTasks;
    private List<Long> unitIds;

    public Long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(Long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }
}
