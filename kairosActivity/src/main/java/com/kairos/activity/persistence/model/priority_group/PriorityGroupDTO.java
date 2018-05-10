package com.kairos.activity.persistence.model.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriorityGroupDTO {
    private BigInteger id;
//    private ShiftSelectionType shiftSelectionType;
   // private boolean singleLongerShift;
    private boolean activated=true;
//    private FeatureRule featureRule;
//    private NotificationWay notificationWay;
    private OpenShiftCancelProcess openShiftCancelProcess;
    private RoundRule roundRule;
    private StaffExcludeFilter staffExcludeFilter;
    private StaffIncludeFilter staffIncludeFilter;
//    private Priority priority;
    private Long countryId;
    private Long unitId;
    private Integer priority;

    public PriorityGroupDTO() {
        //Default Constructor
    }

    public PriorityGroupDTO(Integer priority, BigInteger id, boolean activated,
                            OpenShiftCancelProcess openShiftCancelProcess, RoundRule roundRule, StaffExcludeFilter staffExcludeFilter,
                            StaffIncludeFilter staffIncludeFilter, Long countryId, Long unitId) {
        this.id = id;
        this.activated = activated;
        this.openShiftCancelProcess = openShiftCancelProcess;
        this.roundRule = roundRule;
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

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public OpenShiftCancelProcess getOpenShiftCancelProcess() {
        return openShiftCancelProcess;
    }

    public void setOpenShiftCancelProcess(OpenShiftCancelProcess openShiftCancelProcess) {
        this.openShiftCancelProcess = openShiftCancelProcess;
    }

    public RoundRule getRoundRule() {
        return roundRule;
    }

    public void setRoundRule(RoundRule roundRule) {
        this.roundRule = roundRule;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
