package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.user.staff.unit_position.StaffUnitPositionQueryResult;
import com.kairos.activity.open_shift.priority_group.PriorityGroupDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface PriorityGroupRuleFilter {

//todo remove prioritygroupdto and send only required data in constructor
    public void filter(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO);
}
