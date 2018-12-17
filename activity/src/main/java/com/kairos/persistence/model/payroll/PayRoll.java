package com.kairos.persistence.model.payroll;
/*
 *Created By Pavan on 14/12/18
 *
 */

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class PayRoll extends MongoBaseEntity {
    private String name;
    private int code;
    private boolean active;
    private Set<Long> countryIds=new HashSet<>();

    public PayRoll() {
        //Default Constructor
    }

    public PayRoll(BigInteger id,String name, int code, boolean active) {
        this.id=id;
        this.name = name;
        this.code = code;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Long> getCountryIds() {
        return countryIds;
    }

    public void setCountryIds(Set<Long> countryIds) {
        this.countryIds = countryIds;
    }
}
