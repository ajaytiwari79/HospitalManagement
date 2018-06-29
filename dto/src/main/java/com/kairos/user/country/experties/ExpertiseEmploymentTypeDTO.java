package com.kairos.user.country.experties;

import javax.validation.constraints.AssertTrue;
import java.math.BigInteger;
import java.util.List;

public class ExpertiseEmploymentTypeDTO {
    private Long expertiseId;
    private List<Long> employmentTypeIds;
    public BigInteger includedPlannedTime;
    public BigInteger excludedPlannedTime;

    public ExpertiseEmploymentTypeDTO() {
        // dc
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public List<Long> getEmploymentTypeIds() {
        return employmentTypeIds;
    }

    public void setEmploymentTypeIds(List<Long> employmentTypeIds) {
        this.employmentTypeIds = employmentTypeIds;
    }

    public BigInteger getIncludedPlannedTime() {
        return includedPlannedTime;
    }

    public void setIncludedPlannedTime(BigInteger includedPlannedTime) {
        this.includedPlannedTime = includedPlannedTime;
    }

    public BigInteger getExcludedPlannedTime() {
        return excludedPlannedTime;
    }

    public void setExcludedPlannedTime(BigInteger excludedPlannedTime) {
        this.excludedPlannedTime = excludedPlannedTime;
    }

    @AssertTrue(message = "At least one employmentType should be selected")
    public boolean isValid() {
        return !employmentTypeIds.isEmpty();
    }

}
