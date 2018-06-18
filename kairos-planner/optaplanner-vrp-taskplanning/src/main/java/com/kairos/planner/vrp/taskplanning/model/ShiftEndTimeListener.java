package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDateTime;

//variable listeners have order. make sure it's last or dont use properties that are in listners after its order.
//Note that ASV is called after this listener not before so getShift() would be stale.
//Order for prevTaskOrShift is IRSV > this > ASV
public class ShiftEndTimeListener implements VariableListener<TaskOrShift> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, TaskOrShift task) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, TaskOrShift task) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, TaskOrShift task) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, TaskOrShift taskOrShift) {
        if(taskOrShift instanceof Shift) {
            updateShiftTime(scoreDirector,(Shift)taskOrShift);
        }else{
            updateShiftTime(scoreDirector,(Task)taskOrShift);
        }
    }

    private void updateShiftTime(ScoreDirector scoreDirector, Shift shift) {
        Task task=shift.getNextTask();
        if(task==null){
            updateShiftStartEndTime(shift,null,scoreDirector);
            return;
        }
        int duration = getChainDuration(task);
        LocalDateTime lastTaskEndTime=shift.getStartTime().plusMinutes(duration);
        updateShiftStartEndTime(shift,lastTaskEndTime,scoreDirector);

    }


    private void updateShiftTime(ScoreDirector scoreDirector, Task task) {
        //this is to avoid multiple listener for same task chain
        if(task.getShift()==null )return;//|| task.getNextTask()!=null
        //TODO when task is removed from chain or undomove(x-> null) then shift's time should be updated but task has all variables null so what shift to remove from????????????????????????????????????????
       // if(task.getPlannedStartTime()==null)return; //when task is removed from chain or undomove(x-> null) then shift's time should be updated
        Task firstTask=getFirstTask(task);
        Shift shift= (Shift) firstTask.getPrevTaskOrShift();
        if(shift==null){
            throw new IllegalStateException();
        }
        int duration = getChainDuration(firstTask);
        LocalDateTime lastTaskEndTime=shift.getStartTime().plusMinutes(duration);
        updateShiftStartEndTime(shift,lastTaskEndTime,scoreDirector);

    }

    private int getChainDuration(Task task) {
        int duration=0;
        while (task!=null){
            duration+=task.getDrivingTime()+task.getDuration();
            task=task.getNextTask();
        }
        return duration;
    }

    private Task getFirstTask(Task task) {
        while (task.getPrevTaskOrShift() instanceof Task){
            task=(Task)task.getPrevTaskOrShift();
        }
        return task;
    }

    private void updateShiftStartEndTime(Shift shift, LocalDateTime lastTaskEndTime, ScoreDirector scoreDirector) {
        scoreDirector.beforeVariableChanged(shift,"endTime");
        shift.setEndTime(lastTaskEndTime);
        scoreDirector.afterVariableChanged(shift,"endTime");
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, TaskOrShift task) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, TaskOrShift task) {

    }
}
