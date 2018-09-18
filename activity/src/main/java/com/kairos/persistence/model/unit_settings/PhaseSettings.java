package com.kairos.persistence.model.unit_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class PhaseSettings extends MongoBaseEntity {
    private BigInteger phaseId;
    private String name;
    private String description;
    private boolean staffEligibleForUnderStaffing;
    private boolean staffEligibleForOverStaffing;
    private boolean managementEligibleForUnderStaffing;
    private boolean managementEligibleForOverStaffing;
    private Long unitId;
    private int sequence;

    public PhaseSettings() {
        //Default Constructor
    }

    public PhaseSettings(BigInteger phaseId, String name, String description, boolean staffEligibleForUnderStaffing, boolean staffEligibleForOverStaffing,
                         boolean managementEligibleForUnderStaffing, boolean managementEligibleForOverStaffing, Long unitId, int sequence) {
        this.phaseId = phaseId;
        this.name = name;
        this.description = description;
        this.staffEligibleForUnderStaffing = staffEligibleForUnderStaffing;
        this.staffEligibleForOverStaffing = staffEligibleForOverStaffing;
        this.managementEligibleForUnderStaffing = managementEligibleForUnderStaffing;
        this.managementEligibleForOverStaffing = managementEligibleForOverStaffing;
        this.unitId = unitId;
        this.sequence = sequence;
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStaffEligibleForUnderStaffing() {
        return staffEligibleForUnderStaffing;
    }

    public void setStaffEligibleForUnderStaffing(boolean staffEligibleForUnderStaffing) {
        this.staffEligibleForUnderStaffing = staffEligibleForUnderStaffing;
    }

    public boolean isStaffEligibleForOverStaffing() {
        return staffEligibleForOverStaffing;
    }

    public void setStaffEligibleForOverStaffing(boolean staffEligibleForOverStaffing) {
        this.staffEligibleForOverStaffing = staffEligibleForOverStaffing;
    }

    public boolean isManagementEligibleForUnderStaffing() {
        return managementEligibleForUnderStaffing;
    }

    public void setManagementEligibleForUnderStaffing(boolean managementEligibleForUnderStaffing) {
        this.managementEligibleForUnderStaffing = managementEligibleForUnderStaffing;
    }

    public boolean isManagementEligibleForOverStaffing() {
        return managementEligibleForOverStaffing;
    }

    public void setManagementEligibleForOverStaffing(boolean managementEligibleForOverStaffing) {
        this.managementEligibleForOverStaffing = managementEligibleForOverStaffing;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
