package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ProcessingActivityRelatedDataCategory {


    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotEmpty
    private List<ProcessingActivityRelatedDataElements> dataElements= new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProcessingActivityRelatedDataElements> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<ProcessingActivityRelatedDataElements> dataElements) {
        this.dataElements = dataElements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessingActivityRelatedDataCategory() {
    }
}
