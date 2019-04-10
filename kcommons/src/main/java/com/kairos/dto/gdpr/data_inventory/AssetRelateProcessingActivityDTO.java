package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class AssetRelateProcessingActivityDTO {

    private Set<BigInteger> processingActivities;

    private Set<BigInteger> subProcessingActivities;

}
