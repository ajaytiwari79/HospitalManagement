package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.StaffKpiFilterQueryResult;
import com.kairos.persistence.model.staff.StaffEmploymentQueryResult;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;

import java.util.List;


public interface CustomStaffGraphRepository {

    public List<StaffEmploymentQueryResult> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId);
    List<StaffKpiFilterQueryResult> getStaffsByFilter(Long organizationId, List<Long> unitId, List<Long> employmentType, String startDate, String endDate, List<Long> staffIds,boolean parentOrganization);
}
