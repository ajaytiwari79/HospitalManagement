package com.kairos.planner.vrp.taskplanning.util;

import com.kairos.planner.vrp.taskplanning.model.Task;

import java.util.Objects;

public class VrpPlanningUtil {

    public static boolean isConsecutive(Task task1, Task task2) {
        Task temp=task1;
        while (temp.getPrevTaskOrShift() instanceof Task){
            Task prev = (Task) temp.getPrevTaskOrShift();
            if(!hasSameLocation(prev,temp)) break;
            if(Objects.equals(task1.getId(),task2.getId())){
               return  true;
            }
            temp=prev;
        }
        temp=task1;
        while (temp.getNextTask() !=null){
            Task nextTask = temp.getNextTask();
            if(!hasSameLocation(nextTask,temp)) break;
            if(Objects.equals(task1.getId(),task2.getId())){
                return  true;
            }
            temp=nextTask;
        }
        return false;

    }

    public static  boolean hasSameChain(Task task1,Task task2){
        return !task1.getId().equals(task2.getId()) && task1.getShift().getId().equals(task2.getShift().getId());
    }
    public static boolean hasSameLocation(Task task1,Task task2){
        return Objects.equals(task1.getLattitude(),task2.getLattitude()) &&Objects.equals(task1.getLongitude(),task2.getLongitude());
    }
}
