package com.kairos.persistence.model.user.filter;

import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Date;

/**
 * Created by prerna on 1/5/18.
 */
@QueryResult
public class FilterSelectionQueryResult {

    private String id;
    private String value;
    @DateLong
    private Date startDateMillis;// used in case of expertise
    @DateLong
    private Date endDateMillis; // used in case of expertise.


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

    public Date getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Date startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Date getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Date endDateMillis) {
        this.endDateMillis = endDateMillis;
    }
}
