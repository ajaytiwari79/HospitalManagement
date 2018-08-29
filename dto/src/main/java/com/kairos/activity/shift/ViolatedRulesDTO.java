package com.kairos.activity.shift;

import java.util.List;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class ViolatedRulesDTO {

    private List<RuleViolation> workTimeAggreements;
    private List<RuleViolation> activities;

    public List<RuleViolation> getWorkTimeAggreements() {
        return workTimeAggreements;
    }

    public void setWorkTimeAggreements(List<RuleViolation> workTimeAggreements) {
        this.workTimeAggreements = workTimeAggreements;
    }

    public List<RuleViolation> getActivities() {
        return activities;
    }

    public void setActivities(List<RuleViolation> activities) {
        this.activities = activities;
    }
}
