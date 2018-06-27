package com.kairos.activity.wta.templates;


/**
 * Created by pavan on 18/1/18.
 */

public class PhaseTemplateValue {
    private int phaseId;
    private String phaseName;
    private short staffValue;
    private short managementValue;
    private boolean disabled=true;
    private int optionalFrequency;
    private boolean optional;
    private int sequence;

    public int getOptionalFrequency() {
        return optionalFrequency;
    }

    public void setOptionalFrequency(int optionalFrequency) {
        this.optionalFrequency = optionalFrequency;
    }



    public int getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(int phaseId) {
        this.phaseId = phaseId;
    }




    public PhaseTemplateValue() {
        //Default Constructor
    }

    public PhaseTemplateValue(int phaseId, String phaseName, short staffValue, short managementValue, boolean disabled, int optionalFrequency, boolean optional) {
        this.phaseId = phaseId;
        this.phaseName = phaseName;
        this.staffValue = staffValue;
        this.managementValue = managementValue;
        this.disabled = disabled;
        this.optionalFrequency = optionalFrequency;
        this.optional = optional;
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

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "PhaseTemplateValue{" +
                "phaseName='" + phaseName + '\'' +
                ", staffValue=" + staffValue +
                ", managementValue=" + managementValue +
                ", disabled=" + disabled +
                ", optional=" + optional +
                '}';
    }
}
