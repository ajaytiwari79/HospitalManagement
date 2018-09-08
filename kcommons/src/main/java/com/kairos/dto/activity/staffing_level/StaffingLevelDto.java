package com.kairos.dto.activity.staffing_level;

import com.kairos.dto.activity.staffing_level.absence.AbsenceStaffingLevelDto;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;

import java.util.Map;

/**
 * Created by yatharth on 25/4/18.
 */
public class StaffingLevelDto {

    private Map<String,PresenceStaffingLevelDto> presenceStaffingLevel;
    private Map<String,AbsenceStaffingLevelDto> absenceStaffingLevel;

    public StaffingLevelDto() {

    }
    public StaffingLevelDto(Map<String,PresenceStaffingLevelDto> presenceStaffingLevel,Map<String,AbsenceStaffingLevelDto> absenceStaffingLevel) {
        this.presenceStaffingLevel = presenceStaffingLevel;
        this.absenceStaffingLevel = absenceStaffingLevel;
    }
    public Map<String, PresenceStaffingLevelDto> getPresenceStaffingLevel() {
        return presenceStaffingLevel;
    }

    public void setPresenceStaffingLevel(Map<String, PresenceStaffingLevelDto> presenceStaffingLevel) {
        this.presenceStaffingLevel = presenceStaffingLevel;
    }

    public Map<String, AbsenceStaffingLevelDto> getAbsenceStaffingLevel() {
        return absenceStaffingLevel;
    }

    public void setAbsenceStaffingLevel(Map<String, AbsenceStaffingLevelDto> absenceStaffingLevel) {
        this.absenceStaffingLevel = absenceStaffingLevel;
    }




}
