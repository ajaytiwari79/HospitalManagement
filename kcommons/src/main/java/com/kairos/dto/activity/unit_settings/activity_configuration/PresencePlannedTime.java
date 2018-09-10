package com.kairos.dto.activity.unit_settings.activity_configuration;

import java.math.BigInteger;

public class PresencePlannedTime {
    private BigInteger phaseId;
    private BigInteger staffPlannedTimeId;
    private BigInteger managementPlannedTimeId;

    public PresencePlannedTime() {
        // DC
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public BigInteger getStaffPlannedTimeId() {
        return staffPlannedTimeId;
    }

    public void setStaffPlannedTimeId(BigInteger staffPlannedTimeId) {
        this.staffPlannedTimeId = staffPlannedTimeId;
    }

    public BigInteger getManagementPlannedTimeId() {
        return managementPlannedTimeId;
    }

    public void setManagementPlannedTimeId(BigInteger managementPlannedTimeId) {
        this.managementPlannedTimeId = managementPlannedTimeId;
    }

    public PresencePlannedTime(BigInteger phaseId, BigInteger staffPlannedTimeId, BigInteger managementPlannedTimeId) {
        this.phaseId = phaseId;
        this.staffPlannedTimeId = staffPlannedTimeId;
        this.managementPlannedTimeId = managementPlannedTimeId;
    }
}
