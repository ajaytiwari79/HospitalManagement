package com.kairos.activity.shift;

import java.util.List;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class ViolatedRulesDTO {

    private List<WorkTimeAgreementRuleViolation> workTimeAggreements;
    private List<ActivityRuleViolation> activities;

    public List<WorkTimeAgreementRuleViolation> getWorkTimeAggreements() {
        return workTimeAggreements;
    }

    public void setWorkTimeAggreements(List<WorkTimeAgreementRuleViolation> workTimeAggreements) {
        this.workTimeAggreements = workTimeAggreements;
    }

    public List<ActivityRuleViolation> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityRuleViolation> activities) {
        this.activities = activities;
    }
}
