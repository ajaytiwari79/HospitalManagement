package com.kairos.persistence.model.priority_group;

import com.kairos.dto.activity.open_shift.priority_group.*;
import com.kairos.enums.PriorityGroupName;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

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


    public PriorityGroup() {
        //Default Constructor
    }

    public PriorityGroup(PriorityGroupName name, boolean deActivated, OpenShiftCancelProcess openShiftCancelProcess, RoundRules roundRules, StaffExcludeFilter staffExcludeFilter,
                         StaffIncludeFilter staffIncludeFilter, Long countryId, Long unitId, ScheduledProcess scheduledProcess) {
        this.name=name;
        this.deActivated = deActivated;
        this.roundRules = roundRules;
        this.staffExcludeFilter = staffExcludeFilter;
        this.staffIncludeFilter = staffIncludeFilter;
        this.countryId = countryId;
        this.unitId = unitId;
    }

    public boolean isDeActivated() {
        return deActivated;
    }

    public void setDeActivated(boolean deActivated) {
        this.deActivated = deActivated;
    }

    public RoundRules getRoundRules() {
        return roundRules;
    }

    public void setRoundRules(RoundRules roundRules) {
        this.roundRules = roundRules;
    }

    public StaffExcludeFilter getStaffExcludeFilter() {
        return staffExcludeFilter;
    }

    public void setStaffExcludeFilter(StaffExcludeFilter staffExcludeFilter) {
        this.staffExcludeFilter = staffExcludeFilter;
    }

    public StaffIncludeFilter getStaffIncludeFilter() {
        return staffIncludeFilter;
    }

    public void setStaffIncludeFilter(StaffIncludeFilter staffIncludeFilter) {
        this.staffIncludeFilter = staffIncludeFilter;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public PriorityGroupName getName() {
        return name;
    }

    public void setName(PriorityGroupName name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }


    public DecisionCriteria getDecisionCriteria() {
        return decisionCriteria;
    }

    public void setDecisionCriteria(DecisionCriteria decisionCriteria) {
        this.decisionCriteria = decisionCriteria;
    }

    public BigInteger getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(BigInteger ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public List<Long> getEmploymentTypeIds() {
        return employmentTypeIds;
    }

    public void setEmploymentTypeIds(List<Long> employmentTypeIds) {
        this.employmentTypeIds = employmentTypeIds;
    }

    public List<Long> getExpertiseIds() {
        return expertiseIds;
    }

    public void setExpertiseIds(List<Long> expertiseIds) {
        this.expertiseIds = expertiseIds;
    }
}
