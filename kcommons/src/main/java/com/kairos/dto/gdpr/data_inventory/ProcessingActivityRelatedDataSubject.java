package com.kairos.dto.gdpr.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ProcessingActivityRelatedDataSubject {


    @NotNull
    private Long id;

    @NotNull
    private String name;

    private List<ProcessingActivityRelatedDataCategory> dataCategories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProcessingActivityRelatedDataCategory> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(List<ProcessingActivityRelatedDataCategory> dataCategories) {
        this.dataCategories = dataCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
