package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.StaffUnitPositionQueryResult;
import com.kairos.activity.open_shift.priority_group.StaffIncludeFilter;
import com.kairos.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


public interface CustomStaffGraphRepository {

    public List<StaffUnitPositionQueryResult> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId);
}
