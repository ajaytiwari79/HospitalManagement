package com.kairos.activity.service.priority_group.priority_group_rules;

import com.kairos.response.dto.web.StaffUnitPositionQueryResult;
import com.kairos.response.dto.web.open_shift.priority_group.PriorityGroupDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface PriorityGroupRuleFilter {


    public void filter(Map<BigInteger, List<StaffUnitPositionQueryResult>> openShiftStaffMap, PriorityGroupDTO priorityGroupDTO);
}
