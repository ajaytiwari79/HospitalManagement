package com.kairos.response.dto.web.attendance;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * CreatedBy vipulpandey on 4/9/18
 **/
public class SickSettingsDTO {
    private Long staffId;
    private Long unitId;
    private Long userId;
    private BigInteger activityId;
    private LocalDate startDate;
    private LocalDate endDate;

    public SickSettingsDTO() {
        // dc
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
