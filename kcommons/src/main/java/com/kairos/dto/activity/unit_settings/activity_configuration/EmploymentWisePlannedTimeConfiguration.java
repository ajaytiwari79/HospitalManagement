package com.kairos.dto.activity.unit_settings.activity_configuration;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class EmploymentWisePlannedTimeConfiguration {
    private Long employmentTypeId;
    private List<BigInteger> staffPlannedTimeIds;

}
