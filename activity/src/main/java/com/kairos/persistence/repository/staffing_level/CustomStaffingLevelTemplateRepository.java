package com.kairos.persistence.repository.staffing_level;/*
 *Created By Pavan on 13/8/18
 *
 */

import com.kairos.dto.activity.staffing_level.StaffingLevelTemplateDTO;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public interface CustomStaffingLevelTemplateRepository {

    boolean deleteStaffingLevelTemplate(BigInteger staffingLevelTemplateId);
    List<StaffingLevelTemplateDTO> findByUnitIdDayTypeAndDate(Long unitID, Date proposedStartDate, Date proposedEndDate, List<Long> dayTypeIds, List<String> days);
}
