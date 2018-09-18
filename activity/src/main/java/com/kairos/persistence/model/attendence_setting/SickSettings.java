package com.kairos.persistence.model.attendence_setting;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * CreatedBy vipulpandey on 4/9/18
 **/
@Document
public class SickSettings extends MongoBaseEntity {
    private Long staffId;
    private Long unitId;
    private Long userId;
    private BigInteger activityId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long unitPositionId;   // This is required to find user shifts based on unitPosition Id


    public SickSettings() {
        //
    }

    public SickSettings(Long staffId, Long unitId, Long userId, BigInteger activityId, LocalDate startDate,Long unitPositionId) {
        this.staffId = staffId;
        this.unitId = unitId;
        this.userId = userId;
        this.activityId = activityId;
        this.startDate = startDate;
        this.unitPositionId=unitPositionId;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }
}
