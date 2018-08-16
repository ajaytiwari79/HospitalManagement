package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class ModuleCounter extends MongoBaseEntity {
    private String moduleId;
    private BigInteger counterId;
    private BigInteger countryId;

    public ModuleCounter() {

    }

    public ModuleCounter(BigInteger countryId, String moduleId, BigInteger counterId) {
        this.counterId = counterId;
        this.moduleId = moduleId;
        this.countryId = countryId;
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

    public BigInteger getCountryId() {
        return countryId;
    }

    public void setCountryId(BigInteger countryId) {
        this.countryId = countryId;
    }
}
