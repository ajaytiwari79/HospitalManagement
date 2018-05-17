package com.kairos.persistance.model.filter;

import com.kairos.persistance.model.enums.FilterType;

import java.util.List;

public class FilterSelection {

    private FilterType name;
    private List<String> value;

    public FilterType getName() {
        return name;
    }

    public void setName(FilterType name) {
        this.name = name;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
