package com.kairos.dto.activity.wta.templates;


import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by pavan on 18/1/18.
 */

public class PhaseTemplateValue {
    private BigInteger phaseId;
    private String phaseName;
    private short staffValue;
    private short managementValue;
    private boolean disabled=true;
    private int sequence;
    private boolean staffCanIgnore;
    private boolean managementCanIgnore;


    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }




    public PhaseTemplateValue() {
        //Default Constructor
    }

    public PhaseTemplateValue(BigInteger phaseId, String phaseName, short staffValue, short managementValue, boolean disabled, boolean staffCanIgnore, boolean managementCanIgnore, int sequence) {
        this.phaseId = phaseId;
        this.phaseName = phaseName;
        this.staffValue = staffValue;
        this.managementValue = managementValue;
        this.disabled = disabled;
        this.staffCanIgnore = staffCanIgnore;
        this.managementCanIgnore = managementCanIgnore;
        this.sequence = sequence;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public short getStaffValue() {
        return staffValue;
    }

    public void setStaffValue(short staffValue) {
        this.staffValue = staffValue;
    }

    public short getManagementValue() {
        return managementValue;
    }

    public void setManagementValue(short managementValue) {
        this.managementValue = managementValue;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isStaffCanIgnore() {
        return staffCanIgnore;
    }

    public void setStaffCanIgnore(boolean staffCanIgnore) {
        this.staffCanIgnore = staffCanIgnore;
    }

    public boolean isManagementCanIgnore() {
        return managementCanIgnore;
    }

    public void setManagementCanIgnore(boolean managementCanIgnore) {
        this.managementCanIgnore = managementCanIgnore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhaseTemplateValue)) return false;
        PhaseTemplateValue that = (PhaseTemplateValue) o;
        return phaseId == that.phaseId &&
                staffValue == that.staffValue &&
                managementValue == that.managementValue &&
                disabled == that.disabled &&
                sequence == that.sequence &&
                staffCanIgnore == that.staffCanIgnore &&
                managementCanIgnore == that.managementCanIgnore &&
                Objects.equals(phaseName, that.phaseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phaseId, phaseName, staffValue, managementValue, disabled, sequence, staffCanIgnore, managementCanIgnore);
    }

    @Override
    public String toString() {
        return "PhaseTemplateValue{" +
                "phaseName='" + phaseName + '\'' +
                ", staffValue=" + staffValue +
                ", managementValue=" + managementValue +
                ", disabled=" + disabled +
                '}';
    }
}
