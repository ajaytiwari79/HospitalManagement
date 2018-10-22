package com.kairos.dto.activity.counter.data;

import java.util.List;

public class FilterCriteriaDTO {
    private Long currentCountryId;
    private Long currentUnitId;
    private List<FilterCriteria> filters;
    private List<BasicRequirementDTO> dataRequestList;

    public FilterCriteriaDTO() {
    }

    public FilterCriteriaDTO(List<FilterCriteria> filters) {
        this.filters = filters;
    }

    public Long getCurrentCountryId() {
        return currentCountryId;
    }

    public void setCurrentCountryId(Long currentCountryId) {
        this.currentCountryId = currentCountryId;
    }

    public Long getCurrentUnitId() {
        return currentUnitId;
    }

    public void setCurrentUnitId(Long currentUnitId) {
        this.currentUnitId = currentUnitId;
    }

    public List<FilterCriteria> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterCriteria> filters) {
        this.filters = filters;
    }

    public List<BasicRequirementDTO> getDataRequestList() {
        return dataRequestList;
    }

    public void setDataRequestList(List<BasicRequirementDTO> dataRequestList) {
        this.dataRequestList = dataRequestList;
    }

}
