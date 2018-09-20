package com.kairos.dto.activity.wta.templates;


/**
 * Created by pavan on 18/1/18.
 */

public class PhaseTemplateValue {
    private int phaseId;
    private String phaseName;
    private short staffValue;
    private short managementValue;
    private boolean disabled=true;
    private int sequence;
    private boolean staffCanIgnore;
    private boolean managementCanIgnore;


    public int getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(int phaseId) {
        this.phaseId = phaseId;
    }




    public PhaseTemplateValue() {
        //Default Constructor
    }

    public PhaseTemplateValue(int phaseId, String phaseName, short staffValue, short managementValue, boolean disabled, boolean staffCanIgnore, boolean managementCanIgnore, int sequence) {
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
    public String toString() {
        return "PhaseTemplateValue{" +
                "phaseName='" + phaseName + '\'' +
                ", staffValue=" + staffValue +
                ", managementValue=" + managementValue +
                ", disabled=" + disabled +
                '}';
    }
}
