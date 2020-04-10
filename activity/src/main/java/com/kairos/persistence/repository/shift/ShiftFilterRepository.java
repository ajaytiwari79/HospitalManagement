package com.kairos.persistence.repository.shift;

import com.kairos.enums.FilterType;
import com.kairos.wrapper.shift.StaffShiftDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface ShiftFilterRepository {

   <T> List<StaffShiftDetails> getFilteredShiftsGroupedByStaff(Map<FilterType, Set<T>> values);

}
