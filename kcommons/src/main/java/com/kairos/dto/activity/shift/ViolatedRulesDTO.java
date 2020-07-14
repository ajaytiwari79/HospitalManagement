package com.kairos.dto.activity.shift;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.shift.ShiftEscalationReason;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * @author pradeep
 * @date - 29/8/18
 */

@Getter
@Setter
public class ViolatedRulesDTO {

    private List<WorkTimeAgreementRuleViolation> workTimeAgreements = new ArrayList<>();
    private List<ActivityRuleViolation> activities = new ArrayList<>();
    private Set<ShiftEscalationReason> escalationReasons;
    private boolean escalationResolved;
    private AccessGroupRole escalationCausedBy;
    private boolean draft;

    public List<WorkTimeAgreementRuleViolation> getWorkTimeAgreements() {
        workTimeAgreements=Optional.ofNullable(workTimeAgreements).orElse(new ArrayList<>());
        workTimeAgreements.sort(Comparator.comparing(WorkTimeAgreementRuleViolation::isCanBeIgnore));
        return workTimeAgreements;
    }

    public List<ActivityRuleViolation> getActivities() {
        this.activities =  isNullOrElse(activities,new ArrayList<>());
        return this.activities;
    }
}
