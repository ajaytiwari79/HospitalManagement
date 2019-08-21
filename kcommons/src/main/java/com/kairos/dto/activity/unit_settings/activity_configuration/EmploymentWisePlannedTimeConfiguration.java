package com.kairos.dto.activity.unit_settings.activity_configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EmploymentWisePlannedTimeConfiguration {
    private Long employmentTypeId;
    private List<BigInteger> staffPlannedTimeIds;

    public EmploymentWisePlannedTimeConfiguration(Long employmentTypeId, List<BigInteger> staffPlannedTimeIds) {
        this.employmentTypeId = employmentTypeId;
        this.staffPlannedTimeIds = staffPlannedTimeIds;
    }
}
