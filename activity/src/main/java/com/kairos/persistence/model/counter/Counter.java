package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */
public class Counter extends MongoBaseEntity {

    protected CounterType type;
    protected String title;
    protected boolean counter;
    protected BigInteger primaryCounter; //to directly identify the base counters child
    protected BigInteger parentCounter;  //to identify parent counter
    protected BigInteger categoryId;
    protected List<FilterType> filterTypes;
    protected List<FilterCriteria> criteriaList;
    protected Set<ModuleType> supportedModuleTypes;
    //calculation formula of per KPI
    protected String calculationFormula;
    protected ConfLevel applicableFor;
    protected boolean multiDimensional;

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

    public List<FilterType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(List<FilterType> filterTypes) {
        this.filterTypes = filterTypes;
    }

    public String getCalculationFormula() {
        return calculationFormula;
    }

    public void setCalculationFormula(String calculationFormula) {
        this.calculationFormula = calculationFormula;
    }

    public ConfLevel getApplicableFor() {
        return applicableFor;
    }

    public void setApplicableFor(ConfLevel applicableFor) {
        this.applicableFor = applicableFor;
    }

    public boolean isMultiDimensional() {
        return multiDimensional;
    }

    public void setMultiDimensional(boolean multiDimensional) {
        this.multiDimensional = multiDimensional;
    }
}
