package com.kairos.persistence.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.javers.core.metamodel.annotation.ValueObject;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ValueObject
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

    public ProcessingActivityRelatedDataSubject() {
    }
}
