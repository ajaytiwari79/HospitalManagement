package com.kairos.persistence.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class PlannedTimeType {
    private BigInteger id;
    private String name;
    private Long countryId;
    private String imageName;
}
