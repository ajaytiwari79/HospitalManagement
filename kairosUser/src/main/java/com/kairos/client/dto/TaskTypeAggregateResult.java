package com.kairos.client.dto;

import java.util.List;

/**
 * Created by prabjot on 16/5/17.
 */
public class TaskTypeAggregateResult {

    private long id;
    private List<Integer> taskTypeIds;

    public TaskTypeAggregateResult() {
        //default constructor
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTaskTypeIds(List<Integer> taskTypeIds) {
        this.taskTypeIds = taskTypeIds;
    }

    public long getId() {

        return id;
    }

    public List<Integer> getTaskTypeIds() {
        return taskTypeIds;
    }

    @Override
    public String toString() {
        return "TaskTypeAggregateResult{" +
                "id=" + id +
                ", taskTypeIds=" + taskTypeIds +
                '}';
    }
}
