package com.kairos.dto.activity.attendance;

import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AttendanceDTO {


    private AttendanceDurationDTO duration;
    private List<OrganizationAndReasonCodeDTO> organizationAndReasonCodeDTOS;
    private List<OrganizationCommonDTO> organizationIdAndNameResults;
    private OrganizationCommonDTO organizationCommonDTO;
    private List<ReasonCodeDTO> reasonCode;
    private SickSettingsDTO sickSettings;

    public AttendanceDTO() {
    }

    public AttendanceDTO(AttendanceDurationDTO duration, SickSettingsDTO sickSettings) {
        this.duration = duration;
        this.sickSettings = sickSettings;
    }

    public AttendanceDTO(List<OrganizationCommonDTO> organizationIdAndNameResults, List<ReasonCodeDTO> reasonCode) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
        this.reasonCode = reasonCode;
    }

//    public AttendanceDTO(List<OrganizationAndReasonCodeDTO> organizationAndReasonCodeDTOS) {
//        this.organizationAndReasonCodeDTOS = organizationAndReasonCodeDTOS;
//    }

    public AttendanceDTO(List<ReasonCodeDTO> reasonCode) {
        this.reasonCode = reasonCode;
    }

    public List<OrganizationCommonDTO> getOrganizationIdAndNameResults() {
        return organizationIdAndNameResults;
    }

    public void setOrganizationIdAndNameResults(List<OrganizationCommonDTO> organizationIdAndNameResults) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
    }

    public OrganizationCommonDTO getOrganizationCommonDTO() {
        return organizationCommonDTO;
    }

    public void setOrganizationCommonDTO(OrganizationCommonDTO organizationCommonDTO) {
        this.organizationCommonDTO = organizationCommonDTO;
    }

    public List<OrganizationAndReasonCodeDTO> getOrganizationAndReasonCodeDTOS() {
        return organizationAndReasonCodeDTOS;
    }

    public void setOrganizationAndReasonCodeDTOS(List<OrganizationAndReasonCodeDTO> organizationAndReasonCodeDTOS) {
        this.organizationAndReasonCodeDTOS = organizationAndReasonCodeDTOS;
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
