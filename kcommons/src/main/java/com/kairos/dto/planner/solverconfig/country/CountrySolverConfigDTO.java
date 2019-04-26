package com.kairos.dto.planner.solverconfig.country;

import com.kairos.dto.planner.solverconfig.SolverConfigDTO;

import java.util.List;

public class CountrySolverConfigDTO extends SolverConfigDTO{


    private Long countryId;
    private List<Long> organizationSubServiceIds;

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
