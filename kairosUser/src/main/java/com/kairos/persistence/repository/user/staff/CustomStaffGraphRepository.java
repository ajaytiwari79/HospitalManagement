package com.kairos.persistence.repository.user.staff;

import com.kairos.response.dto.web.open_shift.priority_group.StaffIncludeFilter;
import java.util.List;
import java.util.Set;

public interface CustomStaffGraphRepository {

    public Set<Long> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilter staffIncludeFilter, Long unitId);
}
