package com.kairos.activity.persistence.model.counter;

import com.kairos.persistence.model.enums.FilterType;

import java.util.List;

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
