package com.kairos.dto.activity.unit_settings.activity_configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EmploymentWisePlannedTimeConfiguration implements Serializable {
    private static final long serialVersionUID = 4658229341264692812L;
    private Long employmentTypeId;
    @NotEmpty
    private List<BigInteger> staffPlannedTimeIds;

    public EmploymentWisePlannedTimeConfiguration(Long employmentTypeId, List<BigInteger> staffPlannedTimeIds) {
        this.employmentTypeId = employmentTypeId;
        this.staffPlannedTimeIds = staffPlannedTimeIds;
    }
}
