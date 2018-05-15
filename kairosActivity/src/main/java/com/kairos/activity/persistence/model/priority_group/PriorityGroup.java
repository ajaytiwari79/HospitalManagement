package com.kairos.activity.persistence.model.priority_group;

import com.kairos.activity.enums.PriorityGroup.PriorityGroupName;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class PriorityGroup extends MongoBaseEntity {
    private boolean deActivated;
    private OpenShiftCancelProcess openShiftCancelProcess;
    private RoundRules roundRules;
    private StaffExcludeFilter staffExcludeFilter;
    private StaffIncludeFilter staffIncludeFilter;
    private Long countryId;
    private Long unitId;
    private BigInteger countryParentId;
    private PriorityGroupName name;
    private BigInteger orderId;
    private SchedulerProcess schedulerProcess;

    public PriorityGroup() {
        //Default Constructor
    }

    public PriorityGroup(PriorityGroupName name, boolean deActivated, OpenShiftCancelProcess openShiftCancelProcess, RoundRules roundRules, StaffExcludeFilter staffExcludeFilter,
                         StaffIncludeFilter staffIncludeFilter, Long countryId, Long unitId) {
        this.name=name;
        this.deActivated = deActivated;
        this.openShiftCancelProcess = openShiftCancelProcess;
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



    public OpenShiftCancelProcess getOpenShiftCancelProcess() {
        return openShiftCancelProcess;
    }

    public void setOpenShiftCancelProcess(OpenShiftCancelProcess openShiftCancelProcess) {
        this.openShiftCancelProcess = openShiftCancelProcess;
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

    public BigInteger getOrderId() {
        return orderId;
    }

    public void setOrderId(BigInteger orderId) {
        this.orderId = orderId;
    }

    public SchedulerProcess getSchedulerProcess() {
        return schedulerProcess;
    }

    public void setSchedulerProcess(SchedulerProcess schedulerProcess) {
        this.schedulerProcess = schedulerProcess;
    }
}
