package com.kairos.wrappers;

import java.util.List;

/**
 * Created by prabjot on 16/5/17.
 */
public class TaskTypeAggregateResult {

    private long id;
    private List<String> taskTypeIds;

    public TaskTypeAggregateResult() {
        //default constructor
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getTaskTypeIds() {
        return taskTypeIds;
    }

    public void setTaskTypeIds(List<String> taskTypeIds) {
        this.taskTypeIds = taskTypeIds;
    }

    public long getId() {

        return id;
    }


    @Override
    public String toString() {
        return "TaskTypeAggregateResult{" +
                "id=" + id +
                ", taskTypeIds=" + taskTypeIds +
                '}';
    }
}
