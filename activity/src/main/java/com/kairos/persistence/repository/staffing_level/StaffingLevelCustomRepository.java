package com.kairos.persistence.repository.staffing_level;

import com.kairos.dto.activity.staffing_level.presence.StaffingLevelDTO;
import com.kairos.persistence.model.staffing_level.StaffingLevel;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface StaffingLevelCustomRepository {

    List<StaffingLevel> getStaffingLevelsByUnitIdAndDate(Long unitId, Date startDate, Date endDate);
    List<StaffingLevelDTO> findByUnitIdAndDatesAndActivityId(Long unitId, Date startDate, Date endDate, BigInteger activityId);

    List<HashMap> getStaffingLevelActivities(Long unitId, LocalDate startDate, LocalDate endDate);
}
