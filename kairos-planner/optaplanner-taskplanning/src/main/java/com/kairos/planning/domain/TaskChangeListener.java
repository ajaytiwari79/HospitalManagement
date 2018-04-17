package com.kairos.planning.domain;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TaskChangeListener implements VariableListener<Task> {
    Logger log= LoggerFactory.getLogger(TaskChangeListener.class);
    public static Map<String,Long> values=new HashMap<String,Long>();
    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Task task) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Task task) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Task task) {
        //updateRoute(scoreDirector,task);
        //addDummyTask(scoreDirector,task);
    }

    /*private void addDummyTask(ScoreDirector scoreDirector, Task task) {
        if(task.getNextTask()==null && task.getPreviousTaskOrVehicle()!=null && task.getEmployee()!=null && task.getVehicle()!=null){
            scoreDirector.beforeVariableChanged(task,"route");

            Task nextTask=new Task(1000000+task.getId(),task,task.getVehicle(),task.getEmployee(),0,task.getVehicle().getLocation(),1,TaskType.getDummyTaskType());
            scoreDirector.beforeEntityAdded(nextTask);
            task.setNextTask(nextTask);
            scoreDirector.afterEntityAdded(nextTask);
            scoreDirector.afterVariableChanged(task,"route");
        }
    }*/

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Task task) {
        //updateRoute(scoreDirector,task);
        //addDummyTask(scoreDirector,task);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Task task) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Task task) {

    }

    private void updateRoute(ScoreDirector scoreDirector, Task task) {
       /* if(task==null || task.getPreviousTaskOrVehicle()==null || task.getRoute()!=null) return;
        values.put(task.toString(),values.get(task.toString())!=null?values.get(task.toString())+1:1l);

        scoreDirector.beforeVariableChanged(task,"route");
        Task shadowTask=task;
        if(shadowTask.getPreviousTaskOrVehicle() instanceof Vehicle){
            log.info("Creating route for:"+shadowTask+". Using vehicle="+(Vehicle) shadowTask.getPreviousTaskOrVehicle());
            Route route = new Route(((Vehicle) shadowTask.getPreviousTaskOrVehicle()).getId());
            route.setVehicle((Vehicle) shadowTask.getPreviousTaskOrVehicle());
           // route.setEmployee(shadowTask.getEmployee());
            route.addCheckpoint((Vehicle)shadowTask.getPreviousTaskOrVehicle());
            route.addCheckpoint(shadowTask);
            shadowTask.setRoute(route);
        }else{
            Task lastTaskWithRoute=shadowTask;
            while(lastTaskWithRoute.getRoute()==null && lastTaskWithRoute.getPreviousTaskOrVehicle() instanceof  Task){
                lastTaskWithRoute=(Task) lastTaskWithRoute.getPreviousTaskOrVehicle();
            }
            if(lastTaskWithRoute.getRoute()!=null){
                lastTaskWithRoute.getRoute().addCheckpoint(shadowTask);
                shadowTask.setRoute(lastTaskWithRoute.getRoute());
            }
        }
        scoreDirector.afterVariableChanged(task,"route");*/
    }
}
