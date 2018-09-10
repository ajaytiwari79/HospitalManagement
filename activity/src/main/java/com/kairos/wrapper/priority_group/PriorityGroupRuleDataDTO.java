package com.kairos.wrapper.priority_group;

import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionQueryResult;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PriorityGroupRuleDataDTO {

    private Map<Long,List<Shift>> shiftUnitPositionsMap;
    private Map<BigInteger, OpenShift> openShiftMap;
    private Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap;
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
    public PriorityGroupRuleDataDTO(Map<Long,List<Shift>> shiftUnitPositionsMap, Map<BigInteger, OpenShift> openShiftMap, Map<BigInteger,
            List<StaffUnitPositionQueryResult>> openShiftStaffMap,List<Shift> shifts, List<OpenShiftNotification> openShiftNotifications,
                                    Map<Long,Integer> assignedOpenShiftMap,Set<BigInteger> unavailableActivitySet) {
        this.shiftUnitPositionsMap = shiftUnitPositionsMap;
        this.openShiftMap = openShiftMap;
        this.openShiftStaffMap = openShiftStaffMap;
        this.shifts = shifts;
        this.openShiftNotifications = openShiftNotifications;
        this.assignedOpenShiftMap = assignedOpenShiftMap;
        this.unavailableActivitySet = unavailableActivitySet;
    }

    public Map<Long, List<Shift>> getShiftUnitPositionsMap() {
        return shiftUnitPositionsMap;
    }

    public void setShiftUnitPositionsMap(Map<Long, List<Shift>> shiftUnitPositionsMap) {
        this.shiftUnitPositionsMap = shiftUnitPositionsMap;
    }

    public Map<BigInteger, OpenShift> getOpenShiftMap() {
        return openShiftMap;
    }

    public void setOpenShiftMap(Map<BigInteger, OpenShift> openShiftMap) {
        this.openShiftMap = openShiftMap;
    }

    public Map<BigInteger, List<StaffUnitPositionQueryResult>> getOpenShiftStaffMap() {
        return openShiftStaffMap;
    }

    public void setOpenShiftStaffMap(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap) {
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
