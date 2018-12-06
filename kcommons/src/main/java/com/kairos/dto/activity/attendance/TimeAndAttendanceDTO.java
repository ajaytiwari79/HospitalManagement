package com.kairos.dto.activity.attendance;

import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;

import java.time.LocalDate;
import java.util.List;

public class TimeAndAttendanceDTO {
    private Long userId;
    private Long staffId;
    private LocalDate date;
    private List<AttendanceTimeSlotDTO> attendanceTimeSlot;
    private AttendanceDurationDTO duration;
    private List<OrganizationAndReasonCodeDTO> organizationAndReasonCodes;
    private OrganizationCommonDTO organizationCommonDTO;
    private List<ReasonCodeDTO> reasonCode;
    private SickSettingsDTO sickSettings;

    public TimeAndAttendanceDTO() {
    }

    public TimeAndAttendanceDTO(AttendanceDurationDTO duration, SickSettingsDTO sickSettings) {
        this.duration = duration;
        this.sickSettings = sickSettings;
    }


    public TimeAndAttendanceDTO(List<OrganizationAndReasonCodeDTO> organizationAndReasonCodes, List<ReasonCodeDTO> reasonCode) {
        this.organizationAndReasonCodes = organizationAndReasonCodes;
        this.reasonCode = reasonCode;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<AttendanceTimeSlotDTO> getAttendanceTimeSlot() {
        return attendanceTimeSlot;
    }

    public void setAttendanceTimeSlot(List<AttendanceTimeSlotDTO> attendanceTimeSlot) {
        this.attendanceTimeSlot = attendanceTimeSlot;
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
