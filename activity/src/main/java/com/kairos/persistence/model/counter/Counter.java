package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.*;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class Counter extends MongoBaseEntity {

    private CounterType type;
    private String title;
    private boolean counter;
    private BigInteger primaryCounter; //to directly identify the base counters child
    private BigInteger parentCounter;  //to identify parent counter
    private BigInteger categoryId;
    private List<FilterCriteria> criteriaList;
    private Set<ModuleType> supportedModuleTypes;

    public Counter() {
    }

    public Counter(CounterType type) {
        this.type = type;
    }

    public Counter(String title, CounterType type, boolean counter, BigInteger primaryCounter) {
        this.counter = counter;
        this.primaryCounter = primaryCounter;
        this.type = type;
        this.title = title;
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

    public boolean isCounter() {
        return counter;
    }

    public void setCounter(boolean counter) {
        this.counter = counter;
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

    public Set<ModuleType> getSupportedModuleTypes() {
        return supportedModuleTypes;
    }

    public void setSupportedModuleTypes(Set<ModuleType> supportedModuleTypes) {
        this.supportedModuleTypes = supportedModuleTypes;
    }

    public BigInteger getParentCounter() {
        return parentCounter;
    }

    public void setParentCounter(BigInteger parentCounter) {
        this.parentCounter = parentCounter;
    }
}
