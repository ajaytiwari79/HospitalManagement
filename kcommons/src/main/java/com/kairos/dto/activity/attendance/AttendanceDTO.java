package com.kairos.dto.activity.attendance;

import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.util.List;

public class AttendanceDTO {


    private AttendanceDurationDTO duration;
    private List<OrganizationAndReasonCodeDTO> organizationAndReasonCodes;
    private OrganizationCommonDTO organizationCommonDTO;
    private List<ReasonCodeDTO> reasonCode;
    private SickSettingsDTO sickSettings;

    public AttendanceDTO() {
    }

    public AttendanceDTO(AttendanceDurationDTO duration, SickSettingsDTO sickSettings) {
        this.duration = duration;
        this.sickSettings = sickSettings;
    }


    public AttendanceDTO(List<OrganizationAndReasonCodeDTO> organizationAndReasonCodes, List<ReasonCodeDTO> reasonCode) {
        this.organizationAndReasonCodes = organizationAndReasonCodes;
        this.reasonCode = reasonCode;
    }


    public OrganizationCommonDTO getOrganizationCommonDTO() {
        return organizationCommonDTO;
    }

    public void setOrganizationCommonDTO(OrganizationCommonDTO organizationCommonDTO) {
        this.organizationCommonDTO = organizationCommonDTO;
    }

    public List<OrganizationAndReasonCodeDTO> getOrganizationAndReasonCodes() {
        return organizationAndReasonCodes;
    }

    public void setOrganizationAndReasonCodes(List<OrganizationAndReasonCodeDTO> organizationAndReasonCodes) {
        this.organizationAndReasonCodes = organizationAndReasonCodes;
    }

    public AttendanceDurationDTO getDuration() {
        return duration;
    }

    public void setDuration(AttendanceDurationDTO duration) {
        this.duration = duration;
    }

    public List<ReasonCodeDTO> getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(List<ReasonCodeDTO> reasonCode) {
        this.reasonCode = reasonCode;
    }

    public SickSettingsDTO getSickSettings() {
        return sickSettings;
    }

    public void setSickSettings(SickSettingsDTO sickSettings) {
        this.sickSettings = sickSettings;
    }
}
