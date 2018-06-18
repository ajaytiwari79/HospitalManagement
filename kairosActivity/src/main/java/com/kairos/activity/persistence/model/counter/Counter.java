package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.enums.counter.CounterType;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.util.List;

public class Counter extends MongoBaseEntity {

    private CounterType type;
    private List<FilterCriteria> criteriaList;

    public Counter(){
    }

    public Counter(CounterType type){
        this.type = type;
    }

    public Counter(CounterType type, List<FilterCriteria> criteriaList){
        this.type = type;
        this.criteriaList = criteriaList;
    }

    public CounterType getType() {
        return type;
    }

    public void setType(CounterType type) {
        this.type = type;
    }

    public List<FilterCriteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<FilterCriteria> criteriaList) {
        this.criteriaList = criteriaList;
    }
}
