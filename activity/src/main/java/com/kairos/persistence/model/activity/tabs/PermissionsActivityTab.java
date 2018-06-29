package com.kairos.persistence.model.activity.tabs;

import java.io.Serializable;

public class PermissionsActivityTab implements Serializable {
    private boolean eligibleForCopy;

    public PermissionsActivityTab() {
        //Default Constructor
    }

    public PermissionsActivityTab(boolean eligibleForCopy) {
        this.eligibleForCopy = eligibleForCopy;
    }

    public boolean isEligibleForCopy() {
        return eligibleForCopy;
    }

    public void setEligibleForCopy(boolean eligibleForCopy) {
        this.eligibleForCopy = eligibleForCopy;
    }
}
