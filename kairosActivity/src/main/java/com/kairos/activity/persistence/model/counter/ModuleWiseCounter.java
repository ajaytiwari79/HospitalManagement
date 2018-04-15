package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class ModuleWiseCounter extends MongoBaseEntity {
    private String moduleId;
    private BigInteger counterId;

    public ModuleWiseCounter(){

    }

    public ModuleWiseCounter(String moduleId, BigInteger counterId){
        this.counterId = counterId;
        this.moduleId = moduleId;
    }

    public BigInteger getCounterId() {
        return counterId;
    }

    public void setCounterId(BigInteger counterId) {
        this.counterId = counterId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
