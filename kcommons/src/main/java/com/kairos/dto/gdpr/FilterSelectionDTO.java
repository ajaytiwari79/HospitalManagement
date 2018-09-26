package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterSelectionDTO {


    @NotEmpty(message = "Selection List cannot Empty")
    private List<FilterSelection> filtersData;

    private String moduleId;

    public List<FilterSelection> getFiltersData() {
        return filtersData;
    }

    public void setFiltersData(List<FilterSelection> filtersData) {
        this.filtersData = filtersData;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
