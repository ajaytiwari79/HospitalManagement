package com.kairos.rule_validator.task;

import com.kairos.persistence.model.task.Task;
import com.kairos.rule_validator.task.AbstractTaskSpecification;

/**
 * Created by prabjot on 28/11/17.
 */
public class MergeTaskSpecification extends AbstractTaskSpecification<Task> {

    private boolean mainTask;

    public MergeTaskSpecification(boolean mainTask) {
        this.mainTask = mainTask;
    }

    @Override
    public boolean isSatisfied(Task task) {
        return mainTask == !task.getSubTaskIds().isEmpty();
    }
}
