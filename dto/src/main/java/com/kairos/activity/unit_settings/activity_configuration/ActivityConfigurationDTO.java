package com.kairos.activity.unit_settings.activity_configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.web.cta.PhaseDTO;
import com.kairos.response.dto.web.cta.TimeTypeDTO;
import com.kairos.response.dto.web.presence_type.PresenceTypeDTO;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityConfigurationDTO {
    private BigInteger id;
    private PhaseDTO phase;
    private PresenceTypeDTO managementPlannedTime;
    private PresenceTypeDTO staffPlannedTime;
    private PresenceTypeDTO plannedTime; // for activity.
    private TimeTypeDTO timeType;
    private Boolean exception;

    public ActivityConfigurationDTO() {
        //dc
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public PhaseDTO getPhase() {
        return phase;
    }

    public void setPhase(PhaseDTO phase) {
        this.phase = phase;
    }

    public PresenceTypeDTO getManagementPlannedTime() {
        return managementPlannedTime;
    }

    public void setManagementPlannedTime(PresenceTypeDTO managementPlannedTime) {
        this.managementPlannedTime = managementPlannedTime;
    }

    public PresenceTypeDTO getStaffPlannedTime() {
        return staffPlannedTime;
    }

    public void setStaffPlannedTime(PresenceTypeDTO staffPlannedTime) {
        this.staffPlannedTime = staffPlannedTime;
    }

    public PresenceTypeDTO getPlannedTime() {
        return plannedTime;
    }

    public void setPlannedTime(PresenceTypeDTO plannedTime) {
        this.plannedTime = plannedTime;
    }

    public TimeTypeDTO getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeTypeDTO timeType) {
        this.timeType = timeType;
    }

    public Boolean getException() {
        return exception;
    }

    public void setException(Boolean exception) {
        this.exception = exception;
    }
}

