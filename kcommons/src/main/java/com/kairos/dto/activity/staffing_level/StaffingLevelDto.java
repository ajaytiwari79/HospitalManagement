package com.kairos.dto.activity.staffing_level;

import com.kairos.dto.activity.staffing_level.absence.AbsenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Created by yatharth on 25/4/18.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StaffingLevelDto {

    private Map<String,PresenceStaffingLevelDto> presenceStaffingLevel;
    private Map<String,AbsenceStaffingLevelDto> absenceStaffingLevel;
}
