package com.kairos.dto.activity.open_shift.priority_group;

import com.kairos.enums.PriorityGroupName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Getter
@Setter
@NoArgsConstructor
public class PriorityGroupDTO {
    private BigInteger id;
    private boolean deActivated;
    private RoundRules roundRules;
    private StaffExcludeFilter staffExcludeFilter;
    private StaffIncludeFilter staffIncludeFilter;
    private Long countryId;
    private Long unitId;
    private PriorityGroupName name;
    private BigInteger orderId;
    private BigInteger parentId;
    private DecisionCriteria decisionCriteria;
    private List<Long> employmentTypeIds;
    private List<Long> expertiseIds;
    private BigInteger ruleTemplateId;


    public PriorityGroupDTO(PriorityGroupName name, BigInteger id, boolean deActivated,RoundRules roundRules, StaffExcludeFilter staffExcludeFilter,
                            StaffIncludeFilter staffIncludeFilter, Long countryId, Long unitId) {
        this.id = id;
        this.deActivated = deActivated;
        this.roundRules = roundRules;
        this.staffExcludeFilter = staffExcludeFilter;
        this.staffIncludeFilter = staffIncludeFilter;
        this.countryId = countryId;
        this.unitId = unitId;
    }

    public DecisionCriteria getDecisionCriteria() {
        return decisionCriteria=Optional.ofNullable(decisionCriteria).orElse(new DecisionCriteria());
    }

    public List<Long> getEmploymentTypeIds() {
        return employmentTypeIds=Optional.ofNullable(employmentTypeIds).orElse(new ArrayList<>());
    }

    public List<Long> getExpertiseIds() {
        return expertiseIds=Optional.ofNullable(expertiseIds).orElse(new ArrayList<>());
    }

}
