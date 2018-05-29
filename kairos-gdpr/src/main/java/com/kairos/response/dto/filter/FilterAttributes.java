package com.kairos.response.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterAttributes {


    @NotNullOrEmpty
    private String name;

    @NotNull
    private List<String> value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
