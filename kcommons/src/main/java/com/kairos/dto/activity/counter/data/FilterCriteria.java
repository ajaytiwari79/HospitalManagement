package com.kairos.dto.activity.counter.data;

import com.kairos.enums.FilterType;

import java.util.List;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class FilterCriteria {
    private String name;
    private FilterType type;
    private List<Object> values;

    public FilterCriteria() {
    }

    public FilterCriteria(String name,FilterType type, List<Object> values){
        this.name=name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
