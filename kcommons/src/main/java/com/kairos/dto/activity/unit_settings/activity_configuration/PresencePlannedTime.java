package com.kairos.dto.activity.unit_settings.activity_configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.CommonMessageConstants.PLANNED_TIME_CANNOT_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresencePlannedTime {
    private BigInteger phaseId;
    @Valid
    private List<EmploymentWisePlannedTimeConfiguration> employmentWisePlannedTimeConfigurations;
    @NotEmpty(message = PLANNED_TIME_CANNOT_EMPTY)
    private List<BigInteger> managementPlannedTimeIds;

}
