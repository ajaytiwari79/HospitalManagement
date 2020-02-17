package com.kairos.wrappers.task;


import com.kairos.persistence.model.client_exception.ClientException;
import com.kairos.wrapper.TaskWrapper;

import java.util.List;

/**
 * Created by prabjot on 14/9/17.
 */
public class TaskClientExceptionWrapper {

    private List<TaskWrapper> tasks;
    private List<ClientException> clientExceptions;

    public List<TaskWrapper> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskWrapper> tasks) {
        this.tasks = tasks;
    }

    public List<ClientException> getClientExceptions() {
        return clientExceptions;
    }

    public void setClientExceptions(List<ClientException> clientExceptions) {
        this.clientExceptions = clientExceptions;
    }
}
