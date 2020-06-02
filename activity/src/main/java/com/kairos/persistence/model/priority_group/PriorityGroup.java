package com.kairos.persistence.model.priority_group;

import com.kairos.dto.activity.open_shift.priority_group.DecisionCriteria;
import com.kairos.dto.activity.open_shift.priority_group.RoundRules;
import com.kairos.dto.activity.open_shift.priority_group.StaffExcludeFilter;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilter;
import com.kairos.enums.PriorityGroupName;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
@Getter
@Setter
public class PriorityGroup extends MongoBaseEntity {
    private boolean deActivated;
    private RoundRules roundRules;
    private StaffExcludeFilter staffExcludeFilter;
    private StaffIncludeFilter staffIncludeFilter;
    private Long countryId;
    private Long unitId;
    private BigInteger parentId;
    private Integer priority;
    private BigInteger orderId;
    private PriorityGroupName name;
    private DecisionCriteria decisionCriteria;
    private BigInteger ruleTemplateId;
    private List<Long> employmentTypeIds;
    private List<Long> expertiseIds;
}
