package com.kairos.user.patient.web;

import java.util.List;

/**
 * Created by prabjot on 6/8/17.
 */
public class EscalatedTasksWrapper {

    private long id; // citizen id
    private List<EscalateTaskWrapper> tasks;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<EscalateTaskWrapper> getTasks() {
        return tasks;
    }

    public void setTasks(List<EscalateTaskWrapper> tasks) {
        this.tasks = tasks;
    }
}
