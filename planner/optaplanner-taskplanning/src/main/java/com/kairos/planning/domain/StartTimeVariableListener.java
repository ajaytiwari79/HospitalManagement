package com.kairos.planning.domain;

import com.kairos.planning.executioner.TaskPlanningSolver;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.planning.utils.TaskPlanningUtility;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class StartTimeVariableListener implements VariableListener<Task> {
    public static Logger log= LoggerFactory.getLogger(TaskPlanningSolver.class);

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Task task) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Task task) {
        updateStartTime(scoreDirector,task);
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
        AvailabilityRequest shift=task.getEmployee()==null?null: TaskPlanningUtility.getEmployeeAvailabilityForDay(task.getEmployee(), scoreDirector);

        DateTime plannedStartTime=getPlanableStartTime(task,shift);
        while(task!=null && !Objects.equals(task.getPlannedStartTime(),plannedStartTime)){
            scoreDirector.beforeVariableChanged(task,"plannedStartTime");
            task.setPlannedStartTime(plannedStartTime);
            //log.info("setting time for task {}, {} to {}",task.getId(),task.getTimeWindowsString(),plannedStartTime);
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
            	//log.debug("for first task");
                plannedStartTime=TaskPlanningUtility.getEarliestStartTimeForFirstTask(task,shift);
            }else if(task.getPreviousTaskOrEmployee() instanceof  Task){
            	//log.debug("for chianed task");
                plannedStartTime=TaskPlanningUtility.getEarliestStartTimeForChain(task);
            }
            if(plannedStartTime==null){
                throw new IllegalStateException();
            }
        }
        return plannedStartTime;
    }
    

    @Deprecated
	private DateTime getPlanableStartTime2(Task task,ScoreDirector<TaskPlanningSolution> scoreDirector){
        /*if(task!=null && task.getId()>= TaskPlanningUtility.TASK_ID_SEQUENCE){
            log.info("Planned logical fact");
        }*/
        DateTime plannedStartTime= null;
        if(task!=null) {
            if(task.getPreviousTaskOrEmployee() == null){
                plannedStartTime=task.getInitialStartTime1();
            }else if(task.getPreviousTaskOrEmployee() instanceof Employee){
                Employee employee = (Employee) task.getPreviousTaskOrEmployee();
                boolean possibleToPlan=false;
                Interval intervalIncludingDrive1= new Interval(task.getInitialInterval1().getStart().minusMinutes(task.getDrivingMinutesFromPreviousTaskOrEmployee()),task.getInitialEndTime1());
                //TODO: 
                //if(true){//if(employee.canWorkThisInterval(initialInterval1)){
                if(TaskPlanningUtility.checkEmployeeCanWorkThisInterval(employee, intervalIncludingDrive1, scoreDirector)){
                    possibleToPlan=true;
                    //TODO: need to set time based on  if employee starts first or task starts first.
                    plannedStartTime=task.getInitialStartTime1();
                }
                Interval intervalIncludingDrive2=task.getInitialInterval2()==null?null: new Interval(task.getInitialInterval2().getStart().minusMinutes(task.getDrivingMinutesFromPreviousTaskOrEmployee()),task.getInitialEndTime2());
                if(!possibleToPlan && intervalIncludingDrive2!=null && TaskPlanningUtility.checkEmployeeCanWorkThisInterval(employee, intervalIncludingDrive2, scoreDirector)){//employee.canWorkThisInterval(initialInterval2)
                    plannedStartTime=task.getInitialStartTime2();
                    possibleToPlan=true;
                }
                if(!possibleToPlan){
                    plannedStartTime=task.getInitialStartTime1();
                }
            }else if(task.getPreviousTaskOrEmployee() instanceof  Task){
                boolean found=false;
                DateTime prevEndTime= ((Task)task.getPreviousTaskOrEmployee()).getPlannedEndTime();
                Interval initialInterval1= new Interval(task.getInitialInterval1().getStart().minusMinutes(task.getDrivingMinutesFromPreviousTaskOrEmployee()),task.getInitialEndTime1());
                if(!prevEndTime.isAfter(initialInterval1.getStart())){
                    found=true;
                    //TODO: need to set time based on  if prev tasks end first or this task starts first.
                    plannedStartTime=task.getInitialStartTime1();
                }
                Interval initialInterval2=task.getInitialInterval2()==null?null: new Interval(task.getInitialInterval2().getStart().minusMinutes(task.getDrivingMinutesFromPreviousTaskOrEmployee()),task.getInitialEndTime2());
                if(!found && initialInterval2!=null && !prevEndTime.isAfter(initialInterval2.getStart())){
                    found=true;
                    plannedStartTime=task.getInitialStartTime2();
                }
                if(!found){
                    plannedStartTime= ((Task)task.getPreviousTaskOrEmployee()).getPlannedEndTime().plusMinutes(task.getDrivingMinutesFromPreviousTaskOrEmployee());
                }
            }
            if(plannedStartTime==null){
                plannedStartTime=task.getInitialStartTime1();
            }
        }
        return plannedStartTime;
    }
    @Deprecated
    private DateTime getPossibleStartTime2(Task task){
        DateTime plannedStartTime= null;
        if(task!=null) {
            DateTime initialReachingTime = task.getPreviousTaskOrEmployee() instanceof Task ?
                    ((Task) task.getPreviousTaskOrEmployee()).getPlannedEndTime().plusMinutes(task.getDrivingMinutesFromPreviousTaskOrEmployee())
                    : task.getInitialStartTime1().minusMinutes(task.getDrivingMinutesFromPreviousTaskOrEmployee());
            List<Interval> possibleTaskIntervals = task.getPossibleStartIntervals();
            for (Interval possibleTaskInterval : possibleTaskIntervals) {
                if (possibleTaskInterval.getStart().equals(initialReachingTime) ||
                        possibleTaskInterval.contains(initialReachingTime)) {
                    plannedStartTime = initialReachingTime;
                }
            }
            if (plannedStartTime == null) {
                plannedStartTime = task.getInitialStartTime1();
            }
        }
        return plannedStartTime;
    }
}
