package com.kairos.wrapper.priority_group;

import com.kairos.dto.user.staff.employment.StaffEmploymentQueryResult;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.persistence.model.shift.Shift;

import java.math.BigInteger;
import java.util.*;

public class PriorityGroupRuleDataDTO {

    private Map<Long,List<Shift>> shiftEmploymentsMap;
    private Map<BigInteger, OpenShift> openShiftMap;
    private Map<BigInteger, List<StaffEmploymentQueryResult>> openShiftStaffMap;
    private List<Shift> shifts;
    private List<OpenShiftNotification> openShiftNotifications;
    private Map<Long,Integer> assignedOpenShiftMap;
    private Set<BigInteger> unavailableActivitySet;

    public Set<BigInteger> getUnavailableActivitySet() {
        return unavailableActivitySet;
    }

    public void setUnavailableActivitySet(Set<BigInteger> unavailableActivitySet) {
        this.unavailableActivitySet = unavailableActivitySet;
    }

    public PriorityGroupRuleDataDTO() {

    }

    public PriorityGroupRuleDataDTO(Map<Long,List<Shift>> shiftEmploymentsMap, Map<BigInteger, OpenShift> openShiftMap, Map<BigInteger,
            List<StaffEmploymentQueryResult>> openShiftStaffMap, List<Shift> shifts, List<OpenShiftNotification> openShiftNotifications,
                                    Map<Long,Integer> assignedOpenShiftMap, Set<BigInteger> unavailableActivitySet) {
        this.shiftEmploymentsMap = shiftEmploymentsMap;
        this.openShiftMap = openShiftMap;
        this.openShiftStaffMap = openShiftStaffMap;
        this.shifts = shifts;
        this.openShiftNotifications = openShiftNotifications;
        this.assignedOpenShiftMap = assignedOpenShiftMap;
        this.unavailableActivitySet = unavailableActivitySet;
    }

    public Map<Long, List<Shift>> getShiftEmploymentsMap() {
        return shiftEmploymentsMap;
    }

    public void setShiftEmploymentsMap(Map<Long, List<Shift>> shiftEmploymentsMap) {
        this.shiftEmploymentsMap = shiftEmploymentsMap;
    }

    public Map<BigInteger, OpenShift> getOpenShiftMap() {
        return openShiftMap;
    }

    public void setOpenShiftMap(Map<BigInteger, OpenShift> openShiftMap) {
        this.openShiftMap = openShiftMap;
    }

    public Map<BigInteger, List<StaffEmploymentQueryResult>> getOpenShiftStaffMap() {
        return openShiftStaffMap;
    }

    public void setOpenShiftStaffMap(Map<BigInteger, List<StaffEmploymentQueryResult>> openShiftStaffMap) {
        this.openShiftStaffMap = openShiftStaffMap;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }

    public List<OpenShiftNotification> getOpenShiftNotifications() {
        return openShiftNotifications;
    }

    public void setOpenShiftNotifications(List<OpenShiftNotification> openShiftNotifications) {
        this.openShiftNotifications = openShiftNotifications;
    }

    public Map<Long, Integer> getAssignedOpenShiftMap() {
        return assignedOpenShiftMap;
    }

    public void setAssignedOpenShiftMap(Map<Long, Integer> assignedOpenShiftMap) {
        this.assignedOpenShiftMap = assignedOpenShiftMap;
    }
}
