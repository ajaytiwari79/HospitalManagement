package com.kairos.response.dto.web.wta;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.persistence.model.timetype.TimeTypeDTO;
import com.kairos.response.dto.web.cta.DayTypeDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 26/4/18
 */

public class WTADefaultDataInfoDTO {
    List<ActivityDTO> activityList;
    List<TimeTypeDTO> timeTypes;
    List<DayTypeDTO> dayTypes;
    List<PresenceTypeDTO> presenceTypes;

    public List<ActivityDTO> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<ActivityDTO> activityList) {
        this.activityList = activityList;
    }

    public List<TimeTypeDTO> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeTypeDTO> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public List<DayTypeDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypeDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public List<PresenceTypeDTO> getPresenceTypes() {
        return presenceTypes;
    }

    public void setPresenceTypes(List<PresenceTypeDTO> presenceTypes) {
        this.presenceTypes = presenceTypes;
    }
}
