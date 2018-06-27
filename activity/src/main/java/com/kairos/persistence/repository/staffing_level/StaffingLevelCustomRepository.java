package com.kairos.persistence.repository.staffing_level;

import com.kairos.activity.staffing_level.StaffingLevel;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public interface StaffingLevelCustomRepository {
    void updateStaffingLevel(Long unitId, Date currentDate, LocalTime from , LocalTime to);
    List<StaffingLevel> getStaffingLevelsByUnitIdAndDate(Long unitId, Date startDate, Date endDate);
    StaffingLevel findByUnitIdAndCurrentDateAndDeletedFalseCustom(Long unitId, Date currentDate);

}
