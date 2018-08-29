package com.kairos.persistence.model.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.math.BigInteger;
import java.util.Set;

@CompoundIndex(name = "phase_shiftStatus",def = "{'phaseId','shiftStatus'}",unique = true)
public class ActivityAndShiftStatusSettings extends MongoBaseEntity {
    private BigInteger activityId;
    private BigInteger phaseId;
    private ShiftStatus shiftStatus;
    private Set<Long> accessGroupIds;
    private Long countryId;
    private Long unitId;

    public ActivityAndShiftStatusSettings() {
        //Default Constructor
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
