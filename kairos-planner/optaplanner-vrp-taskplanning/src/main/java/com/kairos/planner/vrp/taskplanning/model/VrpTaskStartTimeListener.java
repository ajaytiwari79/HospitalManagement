package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDateTime;
import java.util.Objects;

//variable listeners have order. make sure it's last or dont use properties that are in listners after its order.
//Note that ASV is called after this listener not before so getShift() would be stale.
//Order for prevTaskOrShift is IRSV > this > ASV
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
        if(task.getPrevTaskOrShift()==null){
            updatePlannedTime(task,null,scoreDirector);
            return;
        }
        if(task.getPrevTaskOrShift() instanceof Shift){
            LocalDateTime plannedDateTime=((Shift)task.getPrevTaskOrShift()).getLocalDate().atTime(Shift.getDefaultShiftStart());
            updatePlannedTime(task,plannedDateTime,scoreDirector);
            return;
        }
        while (task!=null){
            LocalDateTime plannedDateTime =((Task)task.getPrevTaskOrShift()).getPlannedEndTime().plusMinutes(task.getDrivingTime());
            updatePlannedTime(task,plannedDateTime,scoreDirector);
            task=task.getNextTask();
        }
    }

    private void updatePlannedTime(Task task, LocalDateTime plannedDateTime, ScoreDirector scoreDirector){

        if(Objects.equals(task.getPlannedStartTime(),plannedDateTime)) return;
        scoreDirector.beforeVariableChanged(task,"plannedStartTime");
        task.setPlannedStartTime(plannedDateTime);
        scoreDirector.afterVariableChanged(task,"plannedStartTime");
    }



    private void updateStartTimeFromStart(ScoreDirector scoreDirector, Task task) {

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

}
