package com.kairos.repositories.task_type;

import com.kairos.persistence.model.task.Task;

/**
 * Created by prabjot on 1/6/17.
 */
public class ActualPlanningTaskQueryResult {

    private String  id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {

        return id;
    }

    private Task task;

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {

        return task;
    }

    @Override
    public String toString() {
        return "ActualPlanningTaskQueryResult{" +
                "id='" + id + '\'' +
                ", task=" + task +
                '}';
    }
}
