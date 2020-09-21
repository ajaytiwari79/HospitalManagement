package com.kairos.dto.activity.shift;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.enums.shift.ShiftEscalationReason;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author pradeep
 * @date - 30/8/18
 */
@Getter
@Setter
@NoArgsConstructor
public class ShiftViolatedRules{

    //TODO We need proper discussion it should be per phase
    private BigInteger shiftId;
    private List<WorkTimeAgreementRuleViolation> workTimeAgreements;
    private List<ActivityRuleViolation> activities;
    private Set<ShiftEscalationReason> escalationReasons;
    private boolean escalationResolved;
    private AccessGroupRole escalationCausedBy;
    private boolean draft;

    public ShiftViolatedRules(BigInteger shiftId) {
        this.shiftId = shiftId;
    }

    public Set<ShiftEscalationReason> getEscalationReasons() {
        return escalationReasons=Optional.ofNullable(escalationReasons).orElse(new HashSet<>());
    }

    public void setEscalationReasons(Set<ShiftEscalationReason> escalationReasons) {
        this.escalationReasons = ObjectUtils.isCollectionEmpty(escalationReasons) ? new HashSet<>() : escalationReasons;
    }

    public Set<BigInteger> getBreakedRuleTemplateIds(){
        return getWorkTimeAgreements().stream().map(workTimeAgreementRuleViolation -> workTimeAgreementRuleViolation.getRuleTemplateId()).collect(Collectors.toSet());
    }
}
