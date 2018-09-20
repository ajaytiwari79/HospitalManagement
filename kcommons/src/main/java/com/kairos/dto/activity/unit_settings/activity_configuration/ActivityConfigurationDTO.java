package com.kairos.dto.activity.unit_settings.activity_configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.PhaseResponseDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.TimeTypeResponseDTO;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityConfigurationDTO {
    private BigInteger id;
    private PhaseResponseDTO phase;
    private PresenceTypeDTO managementPlannedTime;
    private PresenceTypeDTO staffPlannedTime;
    private PresenceTypeDTO plannedTime; // for activity.
    private TimeTypeResponseDTO timeType;
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

    public PhaseResponseDTO getPhase() {
        return phase;
    }

    public void setPhase(PhaseResponseDTO phase) {
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

    public TimeTypeResponseDTO getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeTypeResponseDTO timeType) {
        this.timeType = timeType;
    }

    public Boolean getException() {
        return exception;
    }

    public void setException(Boolean exception) {
        this.exception = exception;
    }
}

