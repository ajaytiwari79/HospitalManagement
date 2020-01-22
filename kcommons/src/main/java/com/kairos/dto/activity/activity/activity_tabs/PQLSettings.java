package com.kairos.dto.activity.activity.activity_tabs;
/*
 *Created By Pavan on 4/9/18
 *Used for Planning Quality level of planner
 */

import com.kairos.dto.activity.open_shift.DurationField;

import java.util.Optional;

public class PQLSettings {
    public static final String GREEN_COLOR_CODE = "#4caf502e";
    public static final String COLOR_NAME = "Green";
    public static final String YELLOW_COLOR_CODE = "#ffeb3b33";
    public static final String COLOR_NAME1 = "Yellow";
    public static final String COLOR_NAME_1 = COLOR_NAME1;
    public static final String RED_COLOR_CODE = "#ff3b3b33";
    public static final String RED = "Red";
    public static final String COLOR_NAME2 = RED;
    private DurationField approvalTimeInAdvance; // TODO need to rename
    private Float approvalPercentageWithoutMovement;
    private ApprovalCriteria approvalWithMovement;
    private ApprovalCriteria appreciable; // Planning is very good
    private ApprovalCriteria acceptable;  // Planning is fine
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
        appreciable=Optional.ofNullable(appreciable).orElse(new ApprovalCriteria(GREEN_COLOR_CODE, COLOR_NAME));
        appreciable.setColorName(COLOR_NAME);
        appreciable.setColor(GREEN_COLOR_CODE);
        return appreciable;
    }

    public void setAppreciable(ApprovalCriteria appreciable) {
        this.appreciable = appreciable;
    }

    public ApprovalCriteria getAcceptable() {
        acceptable=Optional.ofNullable(acceptable).orElse(new ApprovalCriteria(YELLOW_COLOR_CODE , COLOR_NAME_1));
        acceptable.setColorName(COLOR_NAME1);
        acceptable.setColor(YELLOW_COLOR_CODE);
        return acceptable;
    }

    public void setAcceptable(ApprovalCriteria acceptable) {
        this.acceptable = acceptable;
    }

    public ApprovalCriteria getCritical() {
        critical=Optional.ofNullable(critical).orElse(new ApprovalCriteria( RED_COLOR_CODE, COLOR_NAME2));
        critical.setColorName(COLOR_NAME2);
        critical.setColor(RED_COLOR_CODE);
        return critical;
    }

    public void setCritical(ApprovalCriteria critical) {
        this.critical = critical;
    }
}
