package com.kairos.persistence.model.user.filter;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prerna on 1/5/18.
 */
@QueryResult
public class FilterQueryResult {
    private String name;
    private List<FilterSelectionQueryResult> filterData;
    private String title;

    public FilterQueryResult(){
        // default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterSelectionQueryResult> getFilterData() {
        return filterData;
    }

    public void setFilterData(List<FilterSelectionQueryResult> filterData) {
        this.filterData = filterData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
