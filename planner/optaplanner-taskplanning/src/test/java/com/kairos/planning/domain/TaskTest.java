package com.kairos.planning.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskTest {
    @Test
    public void canEmployeeWork() throws Exception {
    }

    @Test
    public void getOtherTaskTimeDifference() throws Exception {
        Task task = new Task();
        task.setPlannedStartTime(new DateTime().withTimeAtStartOfDay().plusHours(7));
        task.setDuration(10);

        Task prevTask = new Task();
        prevTask.setDuration(20);
        prevTask.setInitialStartTime1(new DateTime().withTimeAtStartOfDay().plusHours(3).plusMinutes(30));
        prevTask.setInitialEndTime1(new DateTime().withTimeAtStartOfDay().plusHours(3).plusMinutes(50));
        prevTask.setInitialStartTime2(new DateTime().withTimeAtStartOfDay().plusHours(6).plusMinutes(30));
        prevTask.setInitialEndTime2(new DateTime().withTimeAtStartOfDay().plusHours(6).plusMinutes(50));

        assertEquals(new Integer(10), task.getOtherTaskTimeDifference(prevTask,true));

        prevTask.setInitialStartTime2(null);
        prevTask.setInitialEndTime2(null);
        assertEquals(new Integer(190), task.getOtherTaskTimeDifference(prevTask,true));


        Task nextTask = new Task();
        nextTask.setDuration(20);
        nextTask.setInitialStartTime1(new DateTime().withTimeAtStartOfDay().plusHours(9).plusMinutes(30));
        nextTask.setInitialEndTime1(new DateTime().withTimeAtStartOfDay().plusHours(9).plusMinutes(50));
        nextTask.setInitialStartTime2(new DateTime().withTimeAtStartOfDay().plusHours(6).plusMinutes(30));
        nextTask.setInitialEndTime2(new DateTime().withTimeAtStartOfDay().plusHours(6).plusMinutes(50));

        assertEquals(new Integer(140), task.getOtherTaskTimeDifference(nextTask,false));
    }

}