package com.kairos.activity.counter;

//Created By Pavan on 3/8/18

import com.kairos.activity.enums.counter.Module;
import com.kairos.enums.CounterType;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class CounterDTO {
    private BigInteger id;
    private CounterType type;
    private String title;
    private boolean treatAsCounter;
    private BigInteger primaryCounter;
    private BigInteger categoryId;
    private List<FilterCriteria> criteriaList;
    private Set<Module> module;

    public CounterDTO() {
        //Default Constructor
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public CounterType getType() {
        return type;
    }

    public void setType(CounterType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
    }

    public List<FilterCriteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<FilterCriteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    public Set<Module> getModule() {
        return module;
    }

    public void setModule(Set<Module> module) {
        this.module = module;
    }
}
