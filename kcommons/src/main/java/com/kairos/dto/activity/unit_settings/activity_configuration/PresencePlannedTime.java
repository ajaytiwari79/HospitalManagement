package com.kairos.dto.activity.unit_settings.activity_configuration;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PresencePlannedTime {
    private BigInteger phaseId;
    private List<EmploymentWisePlannedTimeConfiguration> employmentWisePlannedTimeConfigurations;
    private List<BigInteger> managementPlannedTimeIds;

    public PresencePlannedTime() {
        // DC
    }

    public PresencePlannedTime(BigInteger phaseId, List<EmploymentWisePlannedTimeConfiguration> employmentWisePlannedTimeConfigurations, List<BigInteger> managementPlannedTimeIds) {
        this.phaseId = phaseId;
        this.employmentWisePlannedTimeConfigurations = employmentWisePlannedTimeConfigurations;
        this.managementPlannedTimeIds = managementPlannedTimeIds;
    }
}
