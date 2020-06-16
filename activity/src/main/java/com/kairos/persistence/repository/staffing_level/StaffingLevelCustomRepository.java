package com.kairos.persistence.repository.staffing_level;

import com.kairos.persistence.model.staffing_level.StaffingLevel;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public interface StaffingLevelCustomRepository {

    List<StaffingLevel> getStaffingLevelsByUnitIdAndDate(Long unitId, Date startDate, Date endDate);

}
