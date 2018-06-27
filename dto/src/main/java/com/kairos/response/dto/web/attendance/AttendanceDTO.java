package com.kairos.response.dto.web.attendance;

import java.util.List;

public class AttendanceDTO {

    private AttendanceDuration duration;
    private List<UnitIdAndNameDTO> organizationIdAndNameResults;

    public AttendanceDTO() {
    }

    public AttendanceDTO(AttendanceDuration duration) {
        this.duration = duration;
    }

    public AttendanceDTO(List<UnitIdAndNameDTO> organizationIdAndNameResults) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
    }

    public AttendanceDuration getDuration() {
        return duration;
    }
    public void setDuration(AttendanceDuration duration) {
        this.duration = duration;
    }

    public List<UnitIdAndNameDTO> getOrganizationIdAndNameResults() {
        return organizationIdAndNameResults;
    }

    public void setOrganizationIdAndNameResults(List<UnitIdAndNameDTO> organizationIdAndNameResults) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
    }
}
