package com.kairos.response.dto.web.unit_settings;

import java.math.BigInteger;

public class OpenShiftPhaseSetting {
    private BigInteger phaseId;
    private String phaseName;
    private boolean solveUnderStaffingOverStaffing;

    public OpenShiftPhaseSetting() {
        //Default Constructor
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

    public boolean isSolveUnderStaffingOverStaffing() {
        return solveUnderStaffingOverStaffing;
    }

    public void setSolveUnderStaffingOverStaffing(boolean solveUnderStaffingOverStaffing) {
        this.solveUnderStaffingOverStaffing = solveUnderStaffingOverStaffing;
    }

}
