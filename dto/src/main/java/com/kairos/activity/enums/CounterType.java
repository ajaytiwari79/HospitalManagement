package com.kairos.activity.enums;

import com.kairos.activity.client.counter.CounterTypeDefDTO;

import java.util.ArrayList;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @date: Jun 26th, 2018
 */
public enum CounterType {
    RESTING_HOURS_PER_PRESENCE_DAY("Resting Hours Per Presence Day"),
    SCHEDULED_HOURS_NET("Scheduled Hours-Net"),

    //VRP COUNTER
    TOTAL_KM_DRIVEN_PER_DAY("Total KM Driven Per Day"),
    TASK_UNPLANNED("Total tasks unplanned"),
    TASK_UNPLANNED_HOURS("Total hours of unplanned tasks"),
    TASKS_PER_STAFF("Tasks per staff"),
    ROAD_TIME_PERCENT("Road time in percent of working time"),
    //COMPLETED

    ;
    private String name;

    private CounterType(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public static List<CounterTypeDefDTO> getCounterTypes(){
        List<CounterTypeDefDTO> counterTypes = new ArrayList<>();
        for(CounterType type : CounterType.values()){
            CounterTypeDefDTO typeDef = new CounterTypeDefDTO();
            typeDef.setName(type.getName());
            typeDef.setTypeName(type.toString());
            counterTypes.add(typeDef);
        }
        return counterTypes;
    }
}
