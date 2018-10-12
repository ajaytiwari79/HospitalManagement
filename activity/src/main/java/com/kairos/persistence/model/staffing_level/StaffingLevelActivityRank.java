package com.kairos.persistence.model.staffing_level;
/*
 *Created By Pavan on 9/10/18
 *
 */

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.time.LocalDate;

public class StaffingLevelActivityRank extends MongoBaseEntity {
    private BigInteger activityId;
    private LocalDate staffingLevelDate;
    private BigInteger staffingLevelId;
    private int rank;

    public StaffingLevelActivityRank() {
        //Default Constructor
    }

    public StaffingLevelActivityRank(BigInteger activityId, LocalDate staffingLevelDate, BigInteger staffingLevelId, int rank) {
        this.activityId = activityId;
        this.staffingLevelDate = staffingLevelDate;
        this.staffingLevelId = staffingLevelId;
        this.rank = rank;
    }

    public StaffingLevelActivityRank(BigInteger id, BigInteger activityId, LocalDate staffingLevelDate, BigInteger staffingLevelId, int rank) {
        this.id=id;
        this.activityId = activityId;
        this.staffingLevelDate = staffingLevelDate;
        this.staffingLevelId = staffingLevelId;
        this.rank = rank;
    }



    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public LocalDate getStaffingLevelDate() {
        return staffingLevelDate;
    }

    public void setStaffingLevelDate(LocalDate staffingLevelDate) {
        this.staffingLevelDate = staffingLevelDate;
    }

    public BigInteger getStaffingLevelId() {
        return staffingLevelId;
    }

    public void setStaffingLevelId(BigInteger staffingLevelId) {
        this.staffingLevelId = staffingLevelId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
