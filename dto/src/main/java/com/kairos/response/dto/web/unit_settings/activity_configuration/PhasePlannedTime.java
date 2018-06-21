package com.kairos.response.dto.web.unit_settings.activity_configuration;

import com.kairos.response.dto.web.presence_type.PresenceTypeDTO;

import java.math.BigInteger;

public class PhasePlannedTime {
    private BigInteger phaseId;
    private String phaseName;
    private PresenceTypeDTO staffPlannedTime;
    private PresenceTypeDTO managementPlannedTime;

    public PhasePlannedTime() {
        // DC
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public PresenceTypeDTO getStaffPlannedTime() {
        return staffPlannedTime;
    }

    public void setStaffPlannedTime(PresenceTypeDTO staffPlannedTime) {
        this.staffPlannedTime = staffPlannedTime;
    }

    public PresenceTypeDTO getManagementPlannedTime() {
        return managementPlannedTime;
    }

    public void setManagementPlannedTime(PresenceTypeDTO managementPlannedTime) {
        this.managementPlannedTime = managementPlannedTime;
    }
}
