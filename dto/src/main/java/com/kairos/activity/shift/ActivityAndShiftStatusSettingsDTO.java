package com.kairos.activity.shift;/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.enums.shift.ShiftStatus;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Set;

public class ActivityAndShiftStatusSettingsDTO {
    private BigInteger id;
    @NotNull
    private BigInteger activityId;
    @NotNull
    private BigInteger phaseId;
    @NotNull
    private ShiftStatus shiftStatus;
    private Set<Long> accessGroupIds;

    public ActivityAndShiftStatusSettingsDTO() {
        //Default Constructor
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public ShiftStatus getShiftStatus() {
        return shiftStatus;
    }

    public void setShiftStatus(ShiftStatus shiftStatus) {
        this.shiftStatus = shiftStatus;
    }

    public Set<Long> getAccessGroupIds() {
        return accessGroupIds;
    }

    public void setAccessGroupIds(Set<Long> accessGroupIds) {
        this.accessGroupIds = accessGroupIds;
    }
}
