package com.kairos.dto.activity.shift;

import java.util.*;

/**
 * @author pradeep
 * @date - 29/8/18
 */

public class ViolatedRulesDTO {

    private List<WorkTimeAgreementRuleViolation> workTimeAgreements = new ArrayList<>();
    private List<ActivityRuleViolation> activities = new ArrayList<>();

    public List<WorkTimeAgreementRuleViolation> getWorkTimeAgreements() {
        workTimeAgreements=Optional.ofNullable(workTimeAgreements).orElse(new ArrayList<>());
        workTimeAgreements.sort(Comparator.comparing(WorkTimeAgreementRuleViolation::isCanBeIgnore));
        return workTimeAgreements;
    }

    public void setWorkTimeAgreements(List<WorkTimeAgreementRuleViolation> workTimeAgreements) {
        this.workTimeAgreements = workTimeAgreements;
    }

    public List<ActivityRuleViolation> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityRuleViolation> activities) {
        this.activities = activities;
    }
}
