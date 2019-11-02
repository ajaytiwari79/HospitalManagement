package com.kairos.dto.user.country.experties;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;
import java.util.List;
@Getter
@Setter
public class ExpertiseEmploymentTypeDTO {
    private Long expertiseId;
    private List<Long> employmentTypeIds;
    private BigInteger includedPlannedTime;
    private BigInteger excludedPlannedTime;

    @AssertTrue(message = "At least one employmentType should be selected")
    public boolean isValid() {
        return !employmentTypeIds.isEmpty();
    }

}
