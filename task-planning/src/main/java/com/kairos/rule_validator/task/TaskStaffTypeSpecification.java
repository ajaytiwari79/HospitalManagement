package com.kairos.rule_validator.task;

import com.kairos.persistence.model.task.Task;

/**
 * Created by prabjot on 24/11/17.
 */
public class TaskStaffTypeSpecification extends AbstractTaskSpecification<Task> {

    private boolean excludeEmployees;
    private boolean preferredEmployees;

    public TaskStaffTypeSpecification(boolean excludeEmployees, boolean preferredEmployees) {
        this.excludeEmployees = excludeEmployees;
        this.preferredEmployees = preferredEmployees;
    }

    @Override
    public boolean isSatisfied(Task task) {
        boolean taskHavingPreferredEmployees;
        taskHavingPreferredEmployees = (!preferredEmployees && task.getPrefferedStaffIdsList().isEmpty())?true:
                (preferredEmployees && !task.getPrefferedStaffIdsList().isEmpty())?true:false;

        boolean taskHavingExcludeEmployees;
        taskHavingExcludeEmployees = (!excludeEmployees && task.getForbiddenStaffIdsList().isEmpty())?true:false;

        return taskHavingExcludeEmployees && taskHavingPreferredEmployees;
    }
}
