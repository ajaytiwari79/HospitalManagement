package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionQueryResult;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class PendingRequestRule implements PriorityGroupRuleFilter {

    private List<OpenShiftNotification> openShiftNotifications;

    public PendingRequestRule() {

    }
    public PendingRequestRule(List<OpenShiftNotification> openShiftNotifications) {
        this.openShiftNotifications = openShiftNotifications;
    }
    @Override
    public void filter(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO) {

        Map<Long,List<OpenShiftNotification>> staffOpenShiftNotificationsMap = openShiftNotifications.stream().collect(groupingBy(OpenShiftNotification::getStaffId));
        int maxPendingRequests = priorityGroupDTO.getStaffExcludeFilter().getNumberOfPendingRequest();
        for(Map.Entry<BigInteger,List<StaffUnitPositionQueryResult>> entry: openShiftStaffMap.entrySet()){

            Iterator<StaffUnitPositionQueryResult> staffsUnitPositions = entry.getValue().iterator();
            while(staffsUnitPositions.hasNext()) {
                StaffUnitPositionQueryResult staffUnitPosition = staffsUnitPositions.next();
                if(staffOpenShiftNotificationsMap.containsKey(staffUnitPosition.getStaffId())&&
                        staffOpenShiftNotificationsMap.get(staffUnitPosition.getStaffId()).size()>=maxPendingRequests){
                    staffsUnitPositions.remove();
                }
            }
        }



    }
}
