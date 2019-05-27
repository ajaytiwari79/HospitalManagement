package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDateTime;

//variable listeners have order. make sure it's last or dont use properties that are in listners after its order.
//Note that ASV is called after this listener not before so getShift() would be stale.
//Order for prevTaskOrShift is IRSV > this > ASV
public class VrpTaskStartTimeListener implements VariableListener<Task> {
    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Task task) {
        //Not in use
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Task task) {
        //Not in use
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Task task) {
        //Not in use
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Task task) {
        updateStartTime(scoreDirector,task);

    }


    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Task task) {
        //Not in use
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Task task) {
        //Not in use
    }
    private void updateStartTime(ScoreDirector scoreDirector, Task task) {
        if(task.getPrevTaskOrShift()==null){
            updatePlannedTime(task,null,scoreDirector);
            return;
        }
        if(task.getPrevTaskOrShift() instanceof Shift){
            LocalDateTime plannedDateTime=((Shift)task.getPrevTaskOrShift()).getStartTime();
            updatePlannedTime(task,plannedDateTime,scoreDirector);
            task=task.getNextTask();
            //return;
        }
        while (task!=null){
            LocalDateTime plannedDateTime =((Task)task.getPrevTaskOrShift()).getPlannedEndTime().plusMinutes(task.getDrivingTime());
            updatePlannedTime(task,plannedDateTime,scoreDirector);
            task=task.getNextTask();
        }
    }

    private void updatePlannedTime(Task task, LocalDateTime plannedDateTime, ScoreDirector scoreDirector){
        //TODO we cant bypass here as 540 + 60 or 60+540 will lead or no change... Moreover 540+60 and 60+550 will also lead to same as we round off minutes
       // if(Objects.equals(task.getPlannedStartTime(),plannedDateTime)) return;
        scoreDirector.beforeVariableChanged(task,"plannedStartTime");
        task.setPlannedStartTime(plannedDateTime);
        scoreDirector.afterVariableChanged(task,"plannedStartTime");
    }


    @Deprecated
    private void updateStartTimeFromStart(ScoreDirector scoreDirector, Task task) {

        try{

            if(task.getPrevTaskOrShift()==null || task.getShift()==null){
                updatePlannedTime(task,null,scoreDirector);
                return;
            }
            Task tempTask=task.getShift().getNextTask();
            //LocalDateTime plannedDateTime =((Shift)tempTask.getPrevTaskOrShift()).getLocalDate().atTime(Shift.getDefaultShiftStart());
            LocalDateTime plannedDateTime =((Shift)tempTask.getPrevTaskOrShift()).getStartTime();
            updatePlannedTime(tempTask,plannedDateTime,scoreDirector);
            tempTask=tempTask.getNextTask();
            while (tempTask!=null){
                int duration=tempTask.getPlannedDuration()+tempTask.getDrivingTime();
                plannedDateTime=plannedDateTime.plusMinutes(duration);
                updatePlannedTime(tempTask,plannedDateTime,scoreDirector);
                tempTask=tempTask.getNextTask();
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
