package com.kairos.activity.persistence.enums.counter;

import com.kairos.activity.constants.CounterStore;
import com.kairos.activity.response.dto.counter.CounterTypeDefDTO;
import com.kairos.activity.service.counter.XCounterService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public enum CounterType {
    RESTING_HOURS_PER_PRESENCE_DAY("Resting Hours Per Presence Day"), COUNTER_2("Counter 2"), COUNTER_3("Counter 3");
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
