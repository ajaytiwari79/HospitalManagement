package com.kairos.activity.response.dto.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by vipul on 29/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhaseTemplateValue {
    private Long id;
    private int phaseId;
    private String phaseName;
    private short staffValue=0;
    private short managementValue=0;
    private boolean disabled=true;
    private int optionalFrequency=0;
    private boolean optional=false;

    public PhaseTemplateValue() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(int phaseId) {
        this.phaseId = phaseId;
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

    public int getOptionalFrequency() {
        return optionalFrequency;
    }

    public void setOptionalFrequency(int optionalFrequency) {
        this.optionalFrequency = optionalFrequency;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}