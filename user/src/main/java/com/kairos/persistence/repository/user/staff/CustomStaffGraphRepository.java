package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.StaffUnitPositionQueryResult;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;

import java.util.List;


public interface CustomStaffGraphRepository {

    public List<StaffUnitPositionQueryResult> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId);
}
