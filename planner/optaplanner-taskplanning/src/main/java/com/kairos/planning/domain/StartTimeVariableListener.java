package com.kairos.planning.domain;

import com.kairos.planning.utils.TaskPlanningUtility;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class StartTimeVariableListener implements VariableListener<Task> {
    public static final Logger log= LoggerFactory.getLogger(StartTimeVariableListener.class);

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Task task) {
        //Not in use
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Task task) {
        updateStartTime(scoreDirector,task);
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
        AvailabilityRequest shift=task.getEmployee()==null?null: TaskPlanningUtility.getEmployeeAvailabilityForDay(task.getEmployee(), scoreDirector);

        DateTime plannedStartTime=getPlanableStartTime(task,shift);
        while(task!=null && !Objects.equals(task.getPlannedStartTime(),plannedStartTime)){
            scoreDirector.beforeVariableChanged(task,"plannedStartTime");
            task.setPlannedStartTime(plannedStartTime);
            scoreDirector.afterVariableChanged(task,"plannedStartTime");
            task=task.getNextTask();
            plannedStartTime=getPlanableStartTime(task,shift);
        }
    }
    private DateTime getPlanableStartTime(Task task,AvailabilityRequest shift){
        DateTime plannedStartTime= null;
        if(task!=null) {
            if(task.getPreviousTaskOrEmployee() == null){
                plannedStartTime=task.getInitialStartTime1();
            }else if(task.getPreviousTaskOrEmployee() instanceof Employee){
            	plannedStartTime=TaskPlanningUtility.getEarliestStartTimeForFirstTask(task,shift);
            }else if(task.getPreviousTaskOrEmployee() instanceof  Task){
            	plannedStartTime=TaskPlanningUtility.getEarliestStartTimeForChain(task);
            }
            if(plannedStartTime==null){
                throw new IllegalStateException();
            }
        }
        return plannedStartTime;
    }

}
