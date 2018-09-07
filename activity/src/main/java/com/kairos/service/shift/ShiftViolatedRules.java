package com.kairos.service.shift;

import com.kairos.activity.shift.ActivityRuleViolation;
import com.kairos.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.shift.Shift;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author pradeep
 * @date - 30/8/18
 */
@Document
public class ShiftViolatedRules extends MongoBaseEntity{

    private Shift shift;
    private List<WorkTimeAgreementRuleViolation> workTimeAgreements;
    private List<ActivityRuleViolation> activities;

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public List<WorkTimeAgreementRuleViolation> getWorkTimeAgreements() {
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
