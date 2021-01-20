package com.kairos.persistence.model.activity.tabs.rules_activity_tab;
/*
 *Created By Pavan on 4/9/18
 *Used for Planning Quality level of planner
 */

import com.kairos.annotations.KPermissionField;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.activity_tabs.ApprovalCriteria;
import com.kairos.dto.activity.open_shift.DurationField;

import java.io.Serializable;
import java.util.Optional;

public class PQLSettings implements Serializable {
    @KPermissionField
    private DurationField approvalTimeInAdvance; // TODO need to rename
    @KPermissionField
    private Float approvalPercentageWithoutMovement;
    @KPermissionField
    private ApprovalCriteria approvalWithMovement;
    @KPermissionField
    private ApprovalCriteria appreciable; // Planning is very good
    @KPermissionField
    private ApprovalCriteria acceptable;  // Planning is fine
    @KPermissionField
    private ApprovalCriteria critical;    // Planning is not good/critical

    public PQLSettings() {
        //Default Constructor
    }

    public DurationField getApprovalTimeInAdvance() {
        return approvalTimeInAdvance=Optional.ofNullable(approvalTimeInAdvance).orElse(new DurationField());
    }

    public void setApprovalTimeInAdvance(DurationField approvalTimeInAdvance) {
        this.approvalTimeInAdvance = approvalTimeInAdvance;
    }

    public Float getApprovalPercentageWithoutMovement() {
        return approvalPercentageWithoutMovement;
    }

    public void setApprovalPercentageWithoutMovement(Float approvalPercentageWithoutMovement) {
        this.approvalPercentageWithoutMovement = approvalPercentageWithoutMovement;
    }

    public ApprovalCriteria getApprovalWithMovement() {
        return approvalWithMovement=Optional.ofNullable(approvalWithMovement).orElse(new ApprovalCriteria());
    }

    public void setApprovalWithMovement(ApprovalCriteria approvalWithMovement) {
        this.approvalWithMovement = approvalWithMovement;
    }

    public ApprovalCriteria getAppreciable() {
        appreciable=Optional.ofNullable(appreciable).orElse(new ApprovalCriteria(CommonConstants.GREEN_COLOR_CODE, CommonConstants.COLOR_NAME));
        appreciable.setColorName(CommonConstants.COLOR_NAME);
        appreciable.setColor(CommonConstants.GREEN_COLOR_CODE);
        return appreciable;
    }

    public void setAppreciable(ApprovalCriteria appreciable) {
        this.appreciable = appreciable;
    }

    public ApprovalCriteria getAcceptable() {
        acceptable=Optional.ofNullable(acceptable).orElse(new ApprovalCriteria(CommonConstants.YELLOW_COLOR_CODE , CommonConstants.COLOR_NAME1));
        acceptable.setColorName(CommonConstants.COLOR_NAME1);
        acceptable.setColor(CommonConstants.YELLOW_COLOR_CODE);
        return acceptable;
    }

    public void setAcceptable(ApprovalCriteria acceptable) {
        this.acceptable = acceptable;
    }

    public ApprovalCriteria getCritical() {
        critical=Optional.ofNullable(critical).orElse(new ApprovalCriteria( CommonConstants.RED_COLOR_CODE, CommonConstants.RED));
        critical.setColorName(CommonConstants.RED);
        critical.setColor(CommonConstants.RED_COLOR_CODE);
        return critical;
    }

    public void setCritical(ApprovalCriteria critical) {
        this.critical = critical;
    }
}
