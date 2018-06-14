package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDateTime;

//variable listeners have order. make sure it's last or dont use properties that are in listners after its order.
//Note that ASV is called after this listener not before so getShift() would be stale.
//Order for prevTaskOrShift is IRSV > this > ASV
public class ShiftEndTimeListener implements VariableListener<Task> {

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
        updateShiftTime(scoreDirector,task);
    }

    private void updateShiftTime(ScoreDirector scoreDirector, Task task) {
        if(task.getPlannedStartTime()==null)return;
        Task firstTask=task;
        while (firstTask.getPrevTaskOrShift() instanceof Task){
            firstTask=(Task)firstTask.getPrevTaskOrShift();
        }
        Shift shift= (Shift) firstTask.getPrevTaskOrShift();
        if(shift==null){
            throw new IllegalStateException();
        }
        while (task.getNextTask()!=null){
            task=task.getNextTask();
        }
        LocalDateTime lastTaskEndTime=task.getPlannedEndTime();
        updateShiftStartEndTime(shift,lastTaskEndTime,scoreDirector);

    }

    private void updateShiftStartEndTime(Shift shift, LocalDateTime lastTaskEndTime, ScoreDirector scoreDirector) {
        scoreDirector.beforeVariableChanged(shift,"endTime");
        shift.setEndTime(lastTaskEndTime);
        scoreDirector.afterVariableChanged(shift,"endTime");
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Task task) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Task task) {

    }
}
