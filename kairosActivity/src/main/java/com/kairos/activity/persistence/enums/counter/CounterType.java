package com.kairos.activity.persistence.enums.counter;

import com.kairos.activity.constants.CounterStore;
import com.kairos.activity.service.counter.XCounterService;

import javax.inject.Inject;

public enum CounterType {
    RESTING_HOURS_PER_PRESENCE_DAY("", XCounterService.class);
    private String name;
    private Class classType;
    @Inject
    CounterStore counterStore;
    private CounterType(String name, Class classType){
        this.classType=classType;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

}
