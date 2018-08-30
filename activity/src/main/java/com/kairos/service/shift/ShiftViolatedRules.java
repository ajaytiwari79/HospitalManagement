package com.kairos.service.shift;

import com.kairos.activity.shift.ActivityRuleViolation;
import com.kairos.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.service.MongoBaseService;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 30/8/18
 */
@Document
public class ShiftViolatedRules extends MongoBaseEntity{

    private BigInteger shiftId;
    private List<WorkTimeAgreementRuleViolation> workTimeAggreements;
    private List<ActivityRuleViolation> activities;

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

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
