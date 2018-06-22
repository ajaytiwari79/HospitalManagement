package com.kairos.planner.vrp.taskplanning.util;

import com.kairos.planner.vrp.taskplanning.model.Employee;
import com.kairos.planner.vrp.taskplanning.model.Task;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class VrpPlanningUtilTest {

    @Test
    public void isConsecutive() {
        Task t1= new Task(1234,1234.567,3456.678);
        Task t2= new Task(2345,1234.567,3456.678);
        Task t3= new Task(3456,1234.567,3456.678);
        Task t4= new Task(4567,1234.567,3456.678);
        t1.setNextTask(t2);
        t2.setNextTask(t3);
        t3.setNextTask(t4);
        boolean isCons=VrpPlanningUtil.isConsecutive(t1,t4);
        assertTrue(isCons);
    }

    @Test
    public void hasSameChain() {
    }

    @Test
    public void hasSameLocation() {
    }

    @Test
    public void hasSameSkillset() {
    }

    @Test
    public void getMissingSkills() {
        Task t1= new Task(1234,1234.567,3456.678);
        t1.setSkills(new HashSet<String>(Arrays.asList(new String[]{"El", "Skill1","Skill2"})));
        Employee employee=new Employee();
        employee.setSkills(new HashSet<String>(Arrays.asList(new String[]{"El", "Skill1","Skill2"})));
        assertTrue(VrpPlanningUtil.getMissingSkills(t1,employee)==0);
        t1.getSkills().add("Skill3");
        assertTrue(VrpPlanningUtil.getMissingSkills(t1,employee)==1);
        employee.getSkills().add("Skill3");
        employee.getSkills().add("Skill4");
        assertTrue(VrpPlanningUtil.getMissingSkills(t1,employee)==-1);


    }
}