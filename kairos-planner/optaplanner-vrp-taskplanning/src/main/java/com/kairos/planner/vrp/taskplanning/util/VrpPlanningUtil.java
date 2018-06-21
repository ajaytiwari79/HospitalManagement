package com.kairos.planner.vrp.taskplanning.util;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.planner.vrp.taskplanning.model.Task;
import com.kairos.planner.vrp.taskplanning.solver.VrpTaskPlanningSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class VrpPlanningUtil {
    private static Logger log= LoggerFactory.getLogger(VrpTaskPlanningSolver.class);
    public static boolean isConsecutive(Task task1, Task task2) {
        if(Objects.equals(task1.getNextTask(),task2) || Objects.equals(task1.getPrevTaskOrShift(),task2)){
            return true;
        }
        Task temp=task1;
        while (temp.getPrevTaskOrShift() instanceof Task){
            Task prev = (Task) temp.getPrevTaskOrShift();
            if(!hasSameLocation(prev,temp)) break;
            if(Objects.equals(prev.getId(),task2.getId())){
               return  true;
            }
            temp=prev;
        }
        temp=task1;
        while (temp.getNextTask() !=null){
            Task nextTask = temp.getNextTask();
            if(!hasSameLocation(nextTask,temp)) break;
            if(Objects.equals(nextTask.getId(),task2.getId())){
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

    public static boolean hasSameSkillset(Task task, Task task1) {
        return Objects.equals(task.getSkills(),task1.getSkills());
    }
}
