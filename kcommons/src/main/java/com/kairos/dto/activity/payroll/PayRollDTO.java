package com.kairos.dto.activity.payroll;
/*
 *Created By Pavan on 14/12/18
 *
 */

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class PayRollDTO {
    private BigInteger id;
    private String name;
    private int code;
    private boolean active;
    private Set<Long> countryIds=new HashSet<>();
    private boolean applicableForCountry;

    public PayRollDTO() {
        //Default Constructor
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public boolean isApplicableForCountry() {
        return applicableForCountry;
    }

    public void setApplicableForCountry(boolean applicableForCountry) {
        this.applicableForCountry = applicableForCountry;
    }

    @AssertTrue(message = "Name can't be blank")
    public boolean isValid() {
        return (!StringUtils.isBlank(name));
    }
}
