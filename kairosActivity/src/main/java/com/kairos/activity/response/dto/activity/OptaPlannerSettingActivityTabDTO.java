package com.kairos.activity.response.dto.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.persistence.model.activity.tabs.OptaPlannerSettingActivityTab;

import java.math.BigInteger;

/**
 * Created by vipul on 30/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OptaPlannerSettingActivityTabDTO {
    private BigInteger activityId;
    private boolean eligibleForComputerRecruitment;
    private boolean lockLengthPresent;
    private boolean eligibleToBeForced;

    public OptaPlannerSettingActivityTabDTO() {
    }

    public OptaPlannerSettingActivityTabDTO(BigInteger activityId) {
        this.activityId = activityId;
    }

    public OptaPlannerSettingActivityTabDTO(BigInteger activityId, boolean eligibleForComputerRecruitment) {
        this.activityId = activityId;
        this.eligibleForComputerRecruitment = eligibleForComputerRecruitment;
    }

    public OptaPlannerSettingActivityTabDTO(BigInteger activityId, boolean eligibleForComputerRecruitment, boolean lockLengthPresent, boolean eligibleToBeForced) {
        this.activityId = activityId;
        this.eligibleForComputerRecruitment = eligibleForComputerRecruitment;
        this.lockLengthPresent = lockLengthPresent;
        this.eligibleToBeForced = eligibleToBeForced;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }

    public boolean isEligibleForComputerRecruitment() {
        return eligibleForComputerRecruitment;
    }

    public void setEligibleForComputerRecruitment(boolean eligibleForComputerRecruitment) {
        this.eligibleForComputerRecruitment = eligibleForComputerRecruitment;
    }

    public boolean isLockLengthPresent() {
        return lockLengthPresent;
    }

    public void setLockLengthPresent(boolean lockLengthPresent) {
        this.lockLengthPresent = lockLengthPresent;
    }

    public boolean isEligibleToBeForced() {
        return eligibleToBeForced;
    }

    public void setEligibleToBeForced(boolean eligibleToBeForced) {
        this.eligibleToBeForced = eligibleToBeForced;
    }

    public OptaPlannerSettingActivityTab buildOptaPlannerSettingTab(){
        OptaPlannerSettingActivityTab optaPlannerSettingActivityTab = new OptaPlannerSettingActivityTab(this.eligibleForComputerRecruitment,this.lockLengthPresent,this.eligibleToBeForced);
        return optaPlannerSettingActivityTab;
    }
}
