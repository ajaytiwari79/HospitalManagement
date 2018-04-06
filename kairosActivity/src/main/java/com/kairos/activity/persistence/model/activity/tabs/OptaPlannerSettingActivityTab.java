package com.kairos.activity.persistence.model.activity.tabs;

import java.io.Serializable;

/**
 * Created by vipul on 30/11/17.
 */
public class OptaPlannerSettingActivityTab implements Serializable{

    private boolean eligibleForComputerRecruitment;
    private boolean lockLengthPresent;
    private boolean eligibleToBeForced;

    public OptaPlannerSettingActivityTab() {
    }

    public OptaPlannerSettingActivityTab(boolean eligibleForComputerRecruitment) {
        this.eligibleForComputerRecruitment = eligibleForComputerRecruitment;
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

    public OptaPlannerSettingActivityTab(boolean eligibleForComputerRecruitment, boolean lockLengthPresent, boolean eligibleToBeForced) {
        this.eligibleForComputerRecruitment = eligibleForComputerRecruitment;
        this.lockLengthPresent = lockLengthPresent;
        this.eligibleToBeForced = eligibleToBeForced;
    }
}
