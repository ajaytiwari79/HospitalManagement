package com.kairos.persistence.model.filter;


import com.kairos.dto.gdpr.master_data.ModuleIdDTO;
import com.kairos.enums.gdpr.FilterType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


public class FilterGroup {


    @NotNull
    @NotEmpty
    private List<ModuleIdDTO> accessModule;

    @NotNull
    @NotEmpty
    private List<FilterType> filterTypes;

    private Long countryId;


    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<ModuleIdDTO> getAccessModule() {
        return accessModule;
    }

    public void setAccessModule(List<ModuleIdDTO> accessModule) {
        this.accessModule = accessModule;
    }

    public List<FilterType> getFilterTypes() {
        return filterTypes;
    }

    public void setFilterTypes(List<FilterType> filterTypes) {
        this.filterTypes = filterTypes;
    }

    public FilterGroup() {

    }
    public FilterGroup(List<ModuleIdDTO> accessModule, List<FilterType> filterTypes, Long countryId) {

        this.filterTypes=filterTypes;
        this.accessModule=accessModule;
        this.countryId=countryId;
    }


}
