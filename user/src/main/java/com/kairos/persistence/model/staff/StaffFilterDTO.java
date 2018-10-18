package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.filter.FilterSelection;

import java.util.List;

/**
 * Created by Jasgeet on 13/10/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffFilterDTO {
    private String moduleId;
    private String filterJson;
    private List<FilterSelection> filtersData;
    private long id;
    private String searchText;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getFilterJson() {
        return filterJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilterJson(String filterJson) {
        this.filterJson = filterJson;
    }

    public List<FilterSelection> getFiltersData() {
        return filtersData;
    }

    public void setFiltersData(List<FilterSelection> filtersData) {
        this.filtersData = filtersData;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public StaffFilterDTO() {
    }

    public StaffFilterDTO(String moduleId, String filterJson, String name) {
        this.moduleId = moduleId;
        this.filterJson = filterJson;
        this.name = name;
    }

    public StaffFilterDTO(String moduleId, String filterJson, String name, long id) {
        this.moduleId = moduleId;
        this.filterJson = filterJson;
        this.name = name;
        this.id = id;
    }


}

