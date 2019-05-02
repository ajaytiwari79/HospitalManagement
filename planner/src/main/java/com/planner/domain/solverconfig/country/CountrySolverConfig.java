package com.planner.domain.solverconfig.country;

import com.planner.domain.solverconfig.common.SolverConfig;

import java.util.List;

public class CountrySolverConfig extends SolverConfig{

   
    private Long countryId;
    private List<Long> organizationSubServiceIds;
    
   //~ Getters/Setters

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<Long> getOrganizationSubServiceIds() {
        return organizationSubServiceIds;
    }

    public void setOrganizationSubServiceIds(List<Long> organizationSubServiceIds) {
        this.organizationSubServiceIds = organizationSubServiceIds;
    }
}
