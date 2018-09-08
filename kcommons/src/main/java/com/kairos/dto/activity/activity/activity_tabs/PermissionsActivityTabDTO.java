package com.kairos.dto.activity.activity.activity_tabs;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionsActivityTabDTO {
    private BigInteger activityId;
    private boolean eligibleForCopy;

    public PermissionsActivityTabDTO() {
        //Default Constructor
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public boolean isEligibleForCopy() {
        return eligibleForCopy;
    }

    public void setEligibleForCopy(boolean eligibleForCopy) {
        this.eligibleForCopy = eligibleForCopy;
    }
}
