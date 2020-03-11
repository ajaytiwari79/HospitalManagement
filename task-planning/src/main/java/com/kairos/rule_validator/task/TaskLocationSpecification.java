package com.kairos.rule_validator.task;

import com.kairos.persistence.model.task.Task;

/**
 * Created by prabjot on 28/11/17.
 */
public class TaskLocationSpecification extends AbstractTaskSpecification<Task> {

    private boolean clientPresenceRequired;

    public TaskLocationSpecification(boolean clientPresenceRequired) {
        this.clientPresenceRequired = clientPresenceRequired;
    }

    @Override
    public boolean isSatisfied(Task task) {
        return false;
    }
}
