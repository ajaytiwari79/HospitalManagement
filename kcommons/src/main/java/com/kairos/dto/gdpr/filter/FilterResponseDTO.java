package com.kairos.dto.gdpr.filter;




import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.FilterType;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterResponseDTO {


    private FilterType name;

    private List<FilterAttributes> filterData;

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public FilterType getName() {
        return name;
    }

    public void setName(FilterType name) {
        this.name = name;
    }

    public List<FilterAttributes> getFilterData() {
        return filterData;
    }

    public void setFilterData(List<FilterAttributes> filterData) {
        this.filterData = filterData;
    }


    public FilterResponseDTO() {


    }

    public FilterResponseDTO(FilterType name, String displayName, List<FilterAttributes> filterAttributes) {
        this.name = name;
        this.displayName = displayName;
        this.filterData = filterAttributes;
    }

}
