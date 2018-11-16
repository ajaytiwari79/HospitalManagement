package com.kairos.dto.activity.counter.enums;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @date: Jun 26th, 2018
 */
public enum CounterType {
    RESTING_HOURS_PER_PRESENCE_DAY("Resting Hours Per Presence Day"),
    AVERAGE_RESTING_HOURS_PER_PRESENCE_DAY("Average Resting Hours Per Presence Day"),
    SCHEDULED_HOURS_NET("Scheduled Hours-Net"),

    //VRP COUNTER
    TOTAL_KM_DRIVEN_PER_DAY("Total KM Driven Per Day"),
    TASK_UNPLANNED("Total Tasks Unplanned"),
    TASK_UNPLANNED_HOURS("Total Hours Of Unplanned Tasks"),
    TASKS_PER_STAFF("Tasks Per Staff"),
    ROAD_TIME_PERCENT("Road Time In Percent Of Working Time"),
    TOTAL_TASK_TIME_PERCENT("Total Task Time In Percent Of Working Time"),
    TASKS_COMPLETED_WITHIN_TIME("Tasks Completed Within Time"),
    VALID_BREAK_PERCENT("Breaks Within 11 To 13 From Mon-Thu"),
    FLEXI_TIME_PERCENT("Flexi Time Percent"),
    FLEXI_TIME_TASK_PERCENT("Flex Time Task time"),
    TOTAL_KM_DRIVEN_PER_STAFF("Total Distance Driven Per Staff"),
    TASK_EFFICIENCY("Task Efficiency"),
    WORKING_HOUR_PER_SHIFT("Working Time")
    //COMPLETED

    ;
    private String name;

    private CounterType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
