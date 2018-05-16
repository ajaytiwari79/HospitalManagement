package com.kairos.persistence.model.user.filter;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prerna on 1/5/18.
 */
@QueryResult
public class FilterSelectionQueryResult {

    private String id;
    private String value;

    public FilterSelectionQueryResult(){
        // default constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
