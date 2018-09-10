package com.kairos.dto.activity.cta;

public enum ApprovalWorkFlow {
    NO_APPROVAL_NEEDED("No Approval Needed"),SELF_APPROVAL_ONE_DAY_AFTER("Self Approval 1 day after")
    ,MANAGER_CAN_APPROVE("Manager Can Approve"),PLANER_CAN_APPROVE("Planer Can Approve"),MANAGER_MUST_APPROVE("Manager Must Approve")
    ,PLANER_MUST_APPROVE("Planer Must Approve");
    private String workFlow;
    private ApprovalWorkFlow(String workFlow) {
        this.workFlow = workFlow;
    }
    @Override
    public String toString(){
        return workFlow;
    }
}
