package com.kairos.persistance.model.data_inventory.processing_activity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.javers.core.metamodel.annotation.ValueObject;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ValueObject
public class ProcessingActivityRelatedDataSubject {


    @NotNull
    private BigInteger id;

    private List<ProcessingActivityRelatedDataCategory> dataCategories;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public List<ProcessingActivityRelatedDataCategory> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(List<ProcessingActivityRelatedDataCategory> dataCategories) {
        this.dataCategories = dataCategories;
    }
}
