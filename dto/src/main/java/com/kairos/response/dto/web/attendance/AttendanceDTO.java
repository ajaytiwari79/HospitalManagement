package com.kairos.response.dto.web.attendance;

import com.kairos.activity.persistence.model.staffing_level.Duration;

import java.util.List;
import java.util.Map;

public class AttendanceDTO {

    private AttendanceDuration duration;
    private List<Map<String,Object>> organizationIdAndNameResults;

    public AttendanceDTO() {
    }

    public AttendanceDTO(List<Map<String, Object>> organizationIdAndNameResults) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
    }

    public AttendanceDuration getDuration() {
        return duration;
    }

    public void setDuration(AttendanceDuration duration) {
        this.duration = duration;
    }

    public List<Map<String, Object>> getOrganizationIdAndNameResults() {
        return organizationIdAndNameResults;
    }

    public void setOrganizationIdAndNameResults(List<Map<String, Object>> organizationIdAndNameResults) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
    }
}
