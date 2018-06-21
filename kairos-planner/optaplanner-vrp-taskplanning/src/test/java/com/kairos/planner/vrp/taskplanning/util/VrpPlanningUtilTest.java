package com.kairos.planner.vrp.taskplanning.util;

import com.kairos.planner.vrp.taskplanning.model.Task;
import org.junit.Test;

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
}