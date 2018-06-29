package com.kairos.persistence.model.counter;

import com.kairos.activity.counter.FilterCriteria;
import com.kairos.enums.CounterType;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

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
