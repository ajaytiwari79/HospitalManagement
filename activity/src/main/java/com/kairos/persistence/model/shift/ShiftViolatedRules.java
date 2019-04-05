package com.kairos.persistence.model.shift;

import com.kairos.dto.activity.shift.ActivityRuleViolation;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.enums.shift.ShiftEscalationReason;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.shift.Shift;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author pradeep
 * @date - 30/8/18
 */
@Document
public class ShiftViolatedRules extends MongoBaseEntity{

    //TODO We need proper discussion it should be per phase
    private BigInteger shiftId;
    private List<WorkTimeAgreementRuleViolation> workTimeAgreements;
    private List<ActivityRuleViolation> activities;
    private Set<ShiftEscalationReason> escalationReasons=new HashSet<>();

    public ShiftViolatedRules() {
    }

    public ShiftViolatedRules(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public BigInteger getShiftId() {
        return shiftId;
    }

    public void setShiftId(BigInteger shiftId) {
        this.shiftId = shiftId;
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

    public Set<ShiftEscalationReason> getEscalationReasons() {
        return escalationReasons;
    }

    public void setEscalationReasons(Set<ShiftEscalationReason> escalationReasons) {
        this.escalationReasons = escalationReasons;
    }
}
