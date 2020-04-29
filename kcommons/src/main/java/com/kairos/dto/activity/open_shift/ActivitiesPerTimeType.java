package com.kairos.dto.activity.open_shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ActivitiesPerTimeType {
    private BigInteger timeTypeId;
    private String timeTypeName;
    private List<BigInteger> selectedActivities;
    private boolean selected;
}
