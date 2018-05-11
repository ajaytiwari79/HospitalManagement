package com.kairos.activity.persistence.model.priority_group;

import com.kairos.activity.enums.PriorityGroup.PriorityGroupName;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class PriorityGroup extends MongoBaseEntity {
    //private ShiftSelectionType shiftSelectionType;
    //private boolean singleLongerShift;
    private boolean activated=true;
    //private FeatureRule featureRule;
    //private NotificationWay notificationWay;
    private OpenShiftCancelProcess openShiftCancelProcess;
    private RoundRule roundRule;
    private StaffExcludeFilter staffExcludeFilter;
    private StaffIncludeFilter staffIncludeFilter;
    //private Priority priority;
    private Long countryId;
    private Long unitId;
    private BigInteger countryParentId;
    private PriorityGroupName name;
    private Integer orderId;
    private SchedulerProcess schedulerProcess;

    public PriorityGroup() {
        //Default Constructor
    }

    public PriorityGroup(PriorityGroupName name, boolean activated, OpenShiftCancelProcess openShiftCancelProcess, RoundRule roundRule, StaffExcludeFilter staffExcludeFilter,
                         StaffIncludeFilter staffIncludeFilter, Long countryId, Long unitId) {
        this.name=name;
        this.activated = activated;
        this.openShiftCancelProcess = openShiftCancelProcess;
        this.roundRule = roundRule;
        this.staffExcludeFilter = staffExcludeFilter;
        this.staffIncludeFilter = staffIncludeFilter;
        this.countryId = countryId;
        this.unitId = unitId;
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

    public BigInteger getCountryParentId() {
        return countryParentId;
    }

    public void setCountryParentId(BigInteger countryParentId) {
        this.countryParentId = countryParentId;
    }

    public PriorityGroupName getName() {
        return name;
    }

    public void setName(PriorityGroupName name) {
        this.name = name;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public SchedulerProcess getSchedulerProcess() {
        return schedulerProcess;
    }

    public void setSchedulerProcess(SchedulerProcess schedulerProcess) {
        this.schedulerProcess = schedulerProcess;
    }
}
