package com.kairos.activity.counter.data;

import java.math.BigInteger;

public class BasicRequirementDTO {
    private BigInteger counterId;
    private boolean counterDataRequired;
    private boolean kpiDataRequired;

    public BasicRequirementDTO(){

    }

    public BasicRequirementDTO(BigInteger counterId, boolean counterDataRequired, boolean kpiDataRequired){
        this.counterDataRequired = counterDataRequired;
        this.kpiDataRequired = kpiDataRequired;
        this.counterId = counterId;
    }

    public BigInteger getCounterId() {
        return counterId;
    }

    public void setCounterId(BigInteger counterId) {
        this.counterId = counterId;
    }

    public boolean isCounterDataRequired() {
        return counterDataRequired;
    }

    public void setCounterDataRequired(boolean counterDataRequired) {
        this.counterDataRequired = counterDataRequired;
    }

    public boolean isKpiDataRequired() {
        return kpiDataRequired;
    }

    public void setKpiDataRequired(boolean kpiDataRequired) {
        this.kpiDataRequired = kpiDataRequired;
    }
}
