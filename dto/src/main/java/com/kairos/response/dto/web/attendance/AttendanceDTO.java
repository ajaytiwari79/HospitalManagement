package com.kairos.response.dto.web.attendance;

import java.util.List;

public class AttendanceDTO {

    private AttendanceDurationDTO duration;
    private List<UnitIdAndNameDTO> organizationIdAndNameResults;

    public AttendanceDTO() {
    }

    public AttendanceDTO(AttendanceDurationDTO duration) {
        this.duration = duration;
    }

    public AttendanceDTO(List<UnitIdAndNameDTO> organizationIdAndNameResults) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
    }


    public List<UnitIdAndNameDTO> getOrganizationIdAndNameResults() {
        return organizationIdAndNameResults;
    }

    public void setOrganizationIdAndNameResults(List<UnitIdAndNameDTO> organizationIdAndNameResults) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
    }

    public AttendanceDurationDTO getDuration() {
        return duration;
    }

    public void setDuration(AttendanceDurationDTO duration) {
        this.duration = duration;
    }
}
