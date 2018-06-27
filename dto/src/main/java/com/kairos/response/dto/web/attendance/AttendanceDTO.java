package com.kairos.response.dto.web.attendance;

import com.kairos.activity.persistence.model.staffing_level.Duration;

import java.util.List;
import java.util.Map;

public class AttendanceDTO {

    private AttendanceDuration duration;
    private Map<Long,String> organizationIdAndNameResults;

    public AttendanceDTO() {
    }

    public AttendanceDTO(Map<Long, String> organizationIdAndNameResults) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
    }

    public AttendanceDuration getDuration() {
        return duration;
    }
    public void setDuration(AttendanceDuration duration) {
        this.duration = duration;
    }

    public Map<Long, String> getOrganizationIdAndNameResults() {
        return organizationIdAndNameResults;
    }

    public void setOrganizationIdAndNameResults(Map<Long, String> organizationIdAndNameResults) {
        this.organizationIdAndNameResults = organizationIdAndNameResults;
    }
}
