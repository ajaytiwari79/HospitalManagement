package com.kairos.persistence.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ProcessingActivityRelatedDataElements {


    @NotNull
    private Long id;

    @NotEmpty
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

    public ProcessingActivityRelatedDataElements(@NotNull Long id, @NotEmpty String name) {
        this.id = id;
        this.name = name;
    }

    public ProcessingActivityRelatedDataElements() {
    }
}
