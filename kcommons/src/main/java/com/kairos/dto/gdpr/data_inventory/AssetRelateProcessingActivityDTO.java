package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetRelateProcessingActivityDTO {

    private Set<BigInteger> processingActivities;

    private Set<BigInteger> subProcessingActivities;


    public Set<BigInteger> getProcessingActivities() {
        return processingActivities;
    }

    public void setProcessingActivities(Set<BigInteger> processingActivities) {
        this.processingActivities = processingActivities;
    }

    public Set<BigInteger> getSubProcessingActivities() {
        return subProcessingActivities;
    }

    public void setSubProcessingActivities(Set<BigInteger> subProcessingActivities) {
        this.subProcessingActivities = subProcessingActivities;
    }
}
