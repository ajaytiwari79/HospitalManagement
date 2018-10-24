package com.kairos.dto.planner.solverconfig.country;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;

public class CountrySolverConfigDTO extends SolverConfigDTO{


    private Long countryId;
    private Long parentCountrySolverConfigId;
    private Long organizationServiceId;
    private Long organizationSubServiceId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getParentCountrySolverConfigId() {
        return parentCountrySolverConfigId;
    }

    public void setParentCountrySolverConfigId(Long parentCountrySolverConfigId) {
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
