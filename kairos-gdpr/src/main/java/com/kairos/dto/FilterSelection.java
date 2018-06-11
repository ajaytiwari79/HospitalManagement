package com.kairos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.FilterType;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterSelection {

    @NotNullOrEmpty(message = "error.message.name.cannot.be.null.or.empty")
    private FilterType name;

    @NotEmpty
    @NotNull
    private List<Long> value;

    public FilterSelection() {
        //dv
    }

    public FilterType getName() {
        return name;
    }

    public void setName(FilterType name) {
        this.name = name;
    }

    public List<Long> getValue() {
        return value;
    }

    public void setValue(List<Long> value) {
        this.value = value;
    }
}

