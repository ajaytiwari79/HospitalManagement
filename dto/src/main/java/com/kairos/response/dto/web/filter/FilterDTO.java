package com.kairos.response.dto.web.filter;

import java.util.List;

/**
 * Created by prerna on 30/4/18.
 */
public class FilterDTO {

    private String name;
    private List<FilterDetailDTO> filterData;
    private String title;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterDetailDTO> getFilterData() {
        return filterData;
    }

    public void setFilterData(List<FilterDetailDTO> filterData) {
        this.filterData = filterData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
