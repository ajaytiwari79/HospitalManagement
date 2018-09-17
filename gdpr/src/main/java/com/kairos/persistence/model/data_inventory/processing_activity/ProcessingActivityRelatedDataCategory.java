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
    private BigInteger id;

    @NotEmpty
    private Set<BigInteger> dataElements;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Set<BigInteger> getDataElements() {
        return dataElements;
    }

    public void setDataElements(Set<BigInteger> dataElements) {
        this.dataElements = dataElements;
    }
}
