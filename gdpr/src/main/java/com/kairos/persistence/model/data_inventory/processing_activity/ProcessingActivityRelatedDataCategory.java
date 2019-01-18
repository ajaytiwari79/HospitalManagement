package com.kairos.persistence.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.javers.core.metamodel.annotation.ValueObject;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@ValueObject
public class ProcessingActivityRelatedDataCategory {


    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotEmpty
    private Set<ProcessingActivityRelatedDataElements> dataElements;

    public ProcessingActivityRelatedDataCategory(@NotNull Long id, @NotNull String name, @NotEmpty Set<ProcessingActivityRelatedDataElements> dataElements) {
        this.id = id;
        this.name = name;
        this.dataElements = dataElements;
    }

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

    public Set<ProcessingActivityRelatedDataElements> getDataElements() {
        return dataElements;
    }

    public void setDataElements(Set<ProcessingActivityRelatedDataElements> dataElements) {
        this.dataElements = dataElements;
    }

    public ProcessingActivityRelatedDataCategory() {
    }
}
