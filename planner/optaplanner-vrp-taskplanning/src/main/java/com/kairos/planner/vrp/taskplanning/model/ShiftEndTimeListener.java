package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

//variable listeners have order. make sure it's last or dont use properties that are in listners after its order.
//Note that ASV is called after this listener not before so getShift() would be stale.
//Order for prevTaskOrShift is IRSV > Task.plannedStart > ASV

//Its listening on nextTask which is first in above order hence task.plannedStart() and task.getShift() would be pretty stale.
public class ShiftEndTimeListener implements VariableListener<TaskOrShift> {
    private static Logger log= LoggerFactory.getLogger(ShiftEndTimeListener.class);

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, TaskOrShift task) {
        //Not in use
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, TaskOrShift task) {
        //Not in use
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, TaskOrShift task) {
        //Not in use
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
        if(shift.getId().equals("5b18f9c7016fe33f761ebe16") && shift.getNumberOfTasks() > 20){
            //log.info("tasksnum:"+shift.getNumberOfTasks());
        }
        int duration = shift.getChainDuration();
        LocalDateTime lastTaskEndTime=shift.getStartTime().plusMinutes(duration);
        updateShiftStartEndTime(shift,lastTaskEndTime,scoreDirector);

    }


    private void updateShiftTime(ScoreDirector scoreDirector, Task task) {
        //this is to avoid multiple listener for same task chain
        //if(task.getShift()==null )return;//|| task.getNextTask()!=null
        if(task.getPrevTaskOrShift()==null )return;
        //TODO when task is removed from chain or undomove(x-> null) then shift's time should be updated but task has all variables null so what shift to remove from????????????????????????????????????????
       // if(task.getPlannedStartTime()==null)return; //when task is removed from chain or undomove(x-> null) then shift's time should be updated
        Task firstTask=getFirstTask(task);
        Shift shift= (Shift) firstTask.getPrevTaskOrShift();
        if(shift==null){
            throw new IllegalStateException();
        }
        if(shift.getId().equals("5b18f9c7016fe33f761ebe16")&& shift.getNumberOfTasks() > 20){
            //log.info("tasksnum:"+shift.getNumberOfTasks());
        }
        int duration = shift.getChainDuration();
        LocalDateTime lastTaskEndTime=shift.getStartTime().plusMinutes(duration);
        updateShiftStartEndTime(shift,lastTaskEndTime,scoreDirector);

    }



    private Task getFirstTask(Task task) {
        while (task.getPrevTaskOrShift() instanceof Task){
            task=(Task)task.getPrevTaskOrShift();
        }
        return task;
    }

    private void updateShiftStartEndTime(Shift shift, LocalDateTime lastTaskEndTime, ScoreDirector scoreDirector) {
        scoreDirector.beforeVariableChanged(shift,"plannedEndTime");
        shift.setPlannedEndTime(lastTaskEndTime);
        scoreDirector.afterVariableChanged(shift,"plannedEndTime");
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, TaskOrShift task) {
        //Not in use
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, TaskOrShift task) {
        //Not in use
    }
}
