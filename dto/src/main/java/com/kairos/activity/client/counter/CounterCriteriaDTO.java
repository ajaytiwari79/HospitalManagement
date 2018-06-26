package com.kairos.activity.client.counter;

import java.math.BigInteger;
import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

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
