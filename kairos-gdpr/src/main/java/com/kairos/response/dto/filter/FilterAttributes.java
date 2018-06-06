package com.kairos.response.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterAttributes {


    @NotNull
    private Long id;

    @NotNull
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FilterAttributes() {
    }

    public FilterAttributes(@NotNull Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }
}
