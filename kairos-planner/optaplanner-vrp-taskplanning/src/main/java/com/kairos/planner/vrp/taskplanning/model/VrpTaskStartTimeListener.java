package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDateTime;
import java.util.Objects;

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
        /*if(task.getPrevTaskOrShift() instanceof Shift){
            plannedDateTime=((Shift)task.getPrevTaskOrShift()).getLocalDate().atTime(Shift.getDefaultShiftStart());
            updatePlannedTime(task,plannedDateTime,scoreDirector);
            return;
        }
        while (task!=null){

        }*/
        try{

        if(task.getPrevTaskOrShift()==null || task.getShift()==null){
            updatePlannedTime(task,null,scoreDirector);
            return;
        }
        Task tempTask=task.getShift().getNextTask();
        LocalDateTime plannedDateTime =((Shift)tempTask.getPrevTaskOrShift()).getLocalDate().atTime(Shift.getDefaultShiftStart());
        updatePlannedTime(tempTask,plannedDateTime,scoreDirector);
        tempTask=tempTask.getNextTask();
        while (tempTask!=null){
            double duration=tempTask.getPlannedDuration()+tempTask.getDrivingTime();
            plannedDateTime=plannedDateTime.plusMinutes((long)duration);
            updatePlannedTime(tempTask,plannedDateTime,scoreDirector);
            tempTask=tempTask.getNextTask();
        }

        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private void updatePlannedTime(Task task, LocalDateTime plannedDateTime, ScoreDirector scoreDirector){
        if(Objects.equals(task.getPlannedDateTime(),plannedDateTime)) return;
        scoreDirector.beforeVariableChanged(task,"plannedDateTime");
        task.setPlannedDateTime(plannedDateTime);
        scoreDirector.afterVariableChanged(task,"plannedDateTime");
    }

}
