package com.kairos.dto.activity.unit_settings;

import java.math.BigInteger;

public class OpenShiftPhase {
    private BigInteger phaseId;
    private String phaseName;
    private boolean solveUnderStaffingOverStaffing;

    public OpenShiftPhase() {
        //Default Constructor
    }

    public OpenShiftPhase(BigInteger phaseId, String phaseName, boolean solveUnderStaffingOverStaffing) {
        this.phaseId = phaseId;
        this.phaseName = phaseName;
        this.solveUnderStaffingOverStaffing = solveUnderStaffingOverStaffing;
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
