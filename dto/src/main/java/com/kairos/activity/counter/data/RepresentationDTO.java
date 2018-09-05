package com.kairos.activity.counter.data;

import java.math.BigInteger;

public class RepresentationDTO {
    private BigInteger counterId;
    private Object counterRepresentationData;
    private Object kpiRepresentationData;

    public RepresentationDTO(){

    }

    public RepresentationDTO(BigInteger counterId){
        this.counterId = counterId;
    }

    public BigInteger getCounterId() {
        return counterId;
    }

    public void setCounterId(BigInteger counterId) {
        this.counterId = counterId;
    }

    public Object getCounterRepresentationData() {
        return counterRepresentationData;
    }

    public void setCounterRepresentationData(Object counterRepresentationData) {
        this.counterRepresentationData = counterRepresentationData;
    }

    public Object getKpiRepresentationData() {
        return kpiRepresentationData;
    }

    public void setKpiRepresentationData(Object kpiRepresentationData) {
        this.kpiRepresentationData = kpiRepresentationData;
    }
}
