package com.kairos.persistence.model.activity.tabs;
/*
 *Created By Pavan on 5/9/18
 *
 */

public class ApprovalCriteria {
    private Float approvalPercentage;
    private Short approvalTime; // in Days

    public ApprovalCriteria() {
        //Default Constructor
    }

    public Float getApprovalPercentage() {
        return approvalPercentage;
    }

    public void setApprovalPercentage(Float approvalPercentage) {
        this.approvalPercentage = approvalPercentage;
    }

    public Short getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Short approvalTime) {
        this.approvalTime = approvalTime;
    }
}
