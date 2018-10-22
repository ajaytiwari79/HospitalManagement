package com.planner.domain.country.solverconfig;

import com.planner.domain.common.solverconfig.SolverConfig;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
public class CountrySolverConfig extends SolverConfig{

   
    private Long countryId;
    private BigInteger parentCountrySolverConfigId;//copiedFrom
    private Long organizationServiceId;
    private Long organizationSubServiceId;
    
   //~ Getters/Setters

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public BigInteger getParentCountrySolverConfigId() {
        return parentCountrySolverConfigId;
    }

    public void setParentCountrySolverConfigId(BigInteger parentCountrySolverConfigId) {
        this.parentCountrySolverConfigId = parentCountrySolverConfigId;
    }

    public Long getOrganizationServiceId() {
        return organizationServiceId;
    }

    public void setOrganizationServiceId(Long organizationServiceId) {
        this.organizationServiceId = organizationServiceId;
    }

    public Long getOrganizationSubServiceId() {
        return organizationSubServiceId;
    }

    public void setOrganizationSubServiceId(Long organizationSubServiceId) {
        this.organizationSubServiceId = organizationSubServiceId;
    }
}
