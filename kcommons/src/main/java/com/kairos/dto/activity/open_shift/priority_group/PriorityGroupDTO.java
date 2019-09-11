package com.kairos.dto.activity.open_shift.priority_group;

import com.kairos.enums.PriorityGroupName;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    public PriorityGroupDTO() {
        //Default Constructor
    }

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

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public PriorityGroupName getName() {
        return name;
    }

    public void setName(PriorityGroupName name) {
        this.name = name;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    public DecisionCriteria getDecisionCriteria() {
        return decisionCriteria=Optional.ofNullable(decisionCriteria).orElse(new DecisionCriteria());
    }

    public void setDecisionCriteria(DecisionCriteria decisionCriteria) {
        this.decisionCriteria = decisionCriteria;
    }

    public List<Long> getEmploymentTypeIds() {
        return employmentTypeIds=Optional.ofNullable(employmentTypeIds).orElse(new ArrayList<>());
    }

    public void setEmploymentTypeIds(List<Long> employmentTypeIds) {
        this.employmentTypeIds = employmentTypeIds;
    }

    public List<Long> getExpertiseIds() {
        return expertiseIds=Optional.ofNullable(expertiseIds).orElse(new ArrayList<>());
    }

    public void setExpertiseIds(List<Long> expertiseIds) {
        this.expertiseIds = expertiseIds;
    }

    public BigInteger getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(BigInteger ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }
}
