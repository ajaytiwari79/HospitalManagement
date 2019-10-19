package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author pradeep
 * @date - 29/8/18
 */

@Getter
@Setter
public class ViolatedRulesDTO {

    private List<WorkTimeAgreementRuleViolation> workTimeAgreements = new ArrayList<>();
    private List<ActivityRuleViolation> activities = new ArrayList<>();

    public List<WorkTimeAgreementRuleViolation> getWorkTimeAgreements() {
        workTimeAgreements=Optional.ofNullable(workTimeAgreements).orElse(new ArrayList<>());
        workTimeAgreements.sort(Comparator.comparing(WorkTimeAgreementRuleViolation::isCanBeIgnore));
        return workTimeAgreements;
    }

}
