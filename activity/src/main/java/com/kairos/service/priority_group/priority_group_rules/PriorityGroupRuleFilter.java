package com.kairos.service.priority_group.priority_group_rules;

import com.kairos.dto.activity.open_shift.priority_group.PriorityGroupDTO;
import com.kairos.dto.user.staff.unit_position.StaffUnitPositionQueryResult;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface PriorityGroupRuleFilter {

//todo remove prioritygroupdto and send only required data in constructor
    public void filter(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO);
}
