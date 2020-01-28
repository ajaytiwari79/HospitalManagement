package com.kairos.persistence.repository.user.staff;

import com.kairos.dto.activity.open_shift.priority_group.StaffIncludeFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.staff.StaffEmploymentQueryResult;
import com.kairos.persistence.model.staff.StaffKpiFilterQueryResult;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface CustomStaffGraphRepository {

    List<StaffEmploymentQueryResult> getStaffByPriorityGroupStaffIncludeFilter(StaffIncludeFilterDTO staffIncludeFilterDTO, Long unitId);
    List<StaffKpiFilterQueryResult> getStaffsByFilter(Long organizationId, List<Long> unitId, List<Long> employmentType, String startDate, String endDate, List<Long> staffIds,boolean parentOrganization);
    <T> List<Map> getStaffWithFilters(Long unitId, List<Long> parentOrganizationIds, String moduleId,
                                  Map<FilterType, Set<T>> filters, String searchText, String imagePath,Long loggedInStaffId);
}
