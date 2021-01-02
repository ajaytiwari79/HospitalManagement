package com.kairos.persistence.repository.shift;

import com.kairos.enums.FilterType;
import com.kairos.wrapper.shift.StaffShiftDetailsDTO;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Repository
public interface ShiftFilterRepository  {

    <T> List<StaffShiftDetailsDTO> getFilteredShiftsGroupedByStaff(Set<Long> employmentIds, Map<FilterType, Set<T>> values, Long unitId, Date startDate, Date endDate, boolean includeDateComparison);

    Set<Long> getStaffListAsIdForRealtimeCriteria(Long unitId, Set<String> statuses);

}
