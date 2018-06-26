package com.kairos.activity.client.counter;

import com.kairos.activity.enums.FilterType;

import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class FilterCriteria {
    private FilterType type;
    private List<Object> values;

    public FilterCriteria() {
    }

    public FilterCriteria(FilterType type, List<Object> values){
        this.values = values;
        this.type = type;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }
}
