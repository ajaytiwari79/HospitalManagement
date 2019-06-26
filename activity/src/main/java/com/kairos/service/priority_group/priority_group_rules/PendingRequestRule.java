package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.dto.user.staff.employment.StaffEmploymentQueryResult;
import com.kairos.persistence.model.open_shift.OpenShiftNotification;

import java.math.BigInteger;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class PendingRequestRule implements PriorityGroupRuleFilter {

    private List<OpenShiftNotification> openShiftNotifications;

    public PendingRequestRule() {

    }
    public PendingRequestRule(List<OpenShiftNotification> openShiftNotifications) {
        this.openShiftNotifications = openShiftNotifications;
    }
    @Override
    public void filter(Map<BigInteger, List<StaffEmploymentQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO) {

        Map<Long,List<OpenShiftNotification>> staffOpenShiftNotificationsMap = openShiftNotifications.stream().collect(groupingBy(OpenShiftNotification::getStaffId));
        int maxPendingRequests = priorityGroupDTO.getStaffExcludeFilter().getNumberOfPendingRequest();
        for(Map.Entry<BigInteger,List<StaffEmploymentQueryResult>> entry: openShiftStaffMap.entrySet()){

            Iterator<StaffEmploymentQueryResult> staffsEmployments = entry.getValue().iterator();
            while(staffsEmployments.hasNext()) {
                StaffEmploymentQueryResult staffEmployment = staffsEmployments.next();
                if(staffOpenShiftNotificationsMap.containsKey(staffEmployment.getStaffId())&&
                        staffOpenShiftNotificationsMap.get(staffEmployment.getStaffId()).size()>=maxPendingRequests){
                    staffsEmployments.remove();
                }
            }
        }



    }
}
