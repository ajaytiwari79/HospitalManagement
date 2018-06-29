package com.kairos.spec;

import com.kairos.persistence.model.task.Task;

/**
 * Created by prabjot on 27/11/17.
 */
public class StaffTypeSpecification extends AbstractTaskSpecification<Task>{
    private boolean preferred1Employees;
    private boolean preferred2Employees;

    public StaffTypeSpecification(boolean preferred1Employees, boolean preferred2Employees) {
        this.preferred1Employees = preferred1Employees;
        this.preferred2Employees = preferred2Employees;
    }

    @Override
    public boolean isSatisfied(Task task) {

        return task.getPrefferedStaffIdsList().size() != 0 && preferred1Employees;
    }
}
