package com.kairos.dto.planner.solverconfig.country;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;

public class CountrySolverConfigDTO extends SolverConfigDTO{


    private Long countryId;
    private Long organizationServiceId;
    private Long organizationSubServiceId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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
