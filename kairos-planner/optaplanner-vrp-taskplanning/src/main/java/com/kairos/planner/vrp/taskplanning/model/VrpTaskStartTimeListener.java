package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDateTime;

public class VrpTaskStartTimeListener implements VariableListener<Task> {
    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Task task) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Task task) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Task task) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Task task) {
        updateStartTime(scoreDirector,task);

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Task task) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Task task) {

    }

    private void updateStartTime(ScoreDirector scoreDirector, Task task) {
        LocalDateTime plannedDateTime = null;
        if(task.getPrevTaskOrShift() instanceof Shift){
            plannedDateTime=((Shift)task.getPrevTaskOrShift()).getLocalDate().atTime(Shift.getDefaultShiftStart());
        }else{
            Task temp=task;
        }
        scoreDirector.beforeVariableChanged(task,"plannedDateTime");

        scoreDirector.afterVariableChanged(task,"plannedDateTime");
    }
}
