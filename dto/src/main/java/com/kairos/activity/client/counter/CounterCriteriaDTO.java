package com.kairos.activity.client.counter;

import com.kairos.activity.persistence.model.counter.FilterCriteria;

import java.math.BigInteger;
import java.util.List;

public class CounterCriteriaDTO {
    private BigInteger counterId;
    private List<FilterCriteria> criteriaList;

    public BigInteger getCounterId() {
        return counterId;
    }

    public void setCounterId(BigInteger counterId) {
        this.counterId = counterId;
    }

    public List<FilterCriteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<FilterCriteria> criteriaList) {
        this.criteriaList = criteriaList;
    }
}
