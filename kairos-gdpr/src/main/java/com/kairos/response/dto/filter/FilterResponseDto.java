package com.kairos.response.dto.filter;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.FilterType;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterResponseDto {


    private FilterType name;

    private String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<FilterAttributes> getFilterData() {
        return filterData;
    }

    public void setFilterData(List<FilterAttributes> filterData) {
        this.filterData = filterData;
    }


    public FilterResponseDto() {


    }

    public FilterResponseDto(FilterType name, String title,String displayName,List<FilterAttributes> filterAttributes) {
        this.name = name;
        this.title = title;
        this.displayName=displayName;
        this.filterData = filterAttributes;
    }

}
