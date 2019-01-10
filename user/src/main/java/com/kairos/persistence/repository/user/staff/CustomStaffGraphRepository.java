package com.kairos.persistence.repository.user.staff;

import com.kairos.dto.user.staff.staff.StaffDTO;
import com.kairos.persistence.model.staff.StaffKpiFilterQueryResult;
import com.kairos.persistence.model.staff.StaffUnitPositionQueryResult;
import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;

import java.util.List;


public interface CustomStaffGraphRepository {

    public List<StaffUnitPositionQueryResult> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId);
    List<StaffKpiFilterQueryResult> getStaffsByFilter(Long organizationId, List<Long> unitId, List<Long> employmentType, String startDate, String endDate, List<Long> staffIds,boolean parentOrganization);
}
