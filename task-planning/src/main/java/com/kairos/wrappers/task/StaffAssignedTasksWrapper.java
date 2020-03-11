package com.kairos.wrappers.task;

import com.kairos.wrappers.TaskWrapper;

import java.util.List;

/**
 * Created by prabjot on 6/8/17.
 */
public class StaffAssignedTasksWrapper {

    private long id; // citizen id
    private List<TaskWrapper> tasks;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<TaskWrapper> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskWrapper> tasks) {
        this.tasks = tasks;
    }
}
