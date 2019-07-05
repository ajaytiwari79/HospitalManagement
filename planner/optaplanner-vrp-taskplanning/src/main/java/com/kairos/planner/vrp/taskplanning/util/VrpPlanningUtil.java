package com.kairos.planner.vrp.taskplanning.util;

import com.kairos.planner.vrp.taskplanning.model.Employee;
import com.kairos.planner.vrp.taskplanning.model.Task;
import com.kairos.planner.vrp.taskplanning.solver.VrpTaskPlanningSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class VrpPlanningUtil {
    private static Logger log= LoggerFactory.getLogger(VrpTaskPlanningSolver.class);
    public static boolean isConsecutive(Task task1, Task task2) {
        /*if(Objects.equals(task1.getNextTask(),task2) || Objects.equals(task1.getPrevTaskOrShift(),task2)){
           false return true;
        }*/
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
        return (task1.isShiftBreak() || task2.isShiftBreak()) ||
                Objects.equals(task1.getLatitude(),task2.getLatitude()) &&Objects.equals(task1.getLongitude(),task2.getLongitude());
    }

    public static boolean hasSameSkillset(Task task, Task task1) {
        return Objects.equals(task.getSkills(),task1.getSkills());
    }

    public static int getMissingSkills(Task task, Employee employee) {
        if(task.isShiftBreak()) return 0;
        if(Objects.equals(employee.getSkills(),task.getSkills())){
            return 0;
        }else if(employee.getSkills().containsAll(task.getSkills())){
            //we intent to return -ve
            return task.getSkills().size()-employee.getSkills().size();
        }
        else {
            List<String> list= new ArrayList<>(task.getSkills());
            list.removeAll(employee.getSkills());
            return list.size();
        }
    }
    public static Task getPreviousValidTask(Task task) {
        while (task.getPrevTaskOrShift() instanceof Task){
            Task prevTask = (Task) task.getPrevTaskOrShift();
            if(!prevTask.isShiftBreak()){
                return prevTask;
            }
            task=prevTask;
        }
        return null;
    }
}
