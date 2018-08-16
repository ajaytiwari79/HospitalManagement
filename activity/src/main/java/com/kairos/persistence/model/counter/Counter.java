package com.kairos.persistence.model.counter;

import com.kairos.activity.enums.counter.Module;
import com.kairos.activity.counter.FilterCriteria;
import com.kairos.enums.CounterType;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class Counter extends MongoBaseEntity {

    private CounterType type;
    private String title;
    private boolean treatAsCounter;
    private BigInteger primaryCounter;
    private BigInteger categoryId;
    private List<FilterCriteria> criteriaList;
    private Set<Module> supportedModules =Collections.singleton(Module.OPEN_SHIFT);

    public Counter() {
        //Default Constructor
    }

    public Counter(CounterType type){
        this.type = type;
    }

    public Counter(String title, CounterType type, boolean treatAsCounter, BigInteger primaryCounter){
        this.treatAsCounter = treatAsCounter;
        this.primaryCounter = primaryCounter;
        this.type = type;
        this.title = title;
    }

    public Counter(String title, CounterType type,boolean treatAsCounter, BigInteger primaryCounter, Set<Module> supportedModules) {
        this.type = type;
        this.title = title;
        this.treatAsCounter = treatAsCounter;
        this.primaryCounter = primaryCounter;
        this.supportedModules = supportedModules;
    }

    public Counter(CounterType restingHoursPerPresenceDay, List<FilterCriteria> criteriaList) {
        super();
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

    public boolean isTreatAsCounter() {
        return treatAsCounter;
    }

    public void setTreatAsCounter(boolean treatAsCounter) {
        this.treatAsCounter = treatAsCounter;
    }

    public BigInteger getPrimaryCounter() {
        return primaryCounter;
    }

    public void setPrimaryCounter(BigInteger primaryCounter) {
        this.primaryCounter = primaryCounter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public Set<Module> getSupportedModules() {
        return supportedModules;
    }

    public void setSupportedModules(Set<Module> supportedModules) {
        this.supportedModules = supportedModules;
    }
}
