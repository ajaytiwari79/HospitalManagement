package com.kairos.activity.wta.basic_details;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.time_type.TimeTypeDTO;
import com.kairos.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.user.country.time_slot.TimeSlotDTO;
import com.kairos.activity.presence_type.PresenceTypeDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 26/4/18
 */

public class WTADefaultDataInfoDTO {
    private List<ActivityDTO> activityList = new ArrayList<>();
    private List<TimeTypeDTO> timeTypes = new ArrayList<>();
    private List<DayTypeDTO> dayTypes = new ArrayList<>();
    private List<PresenceTypeDTO> presenceTypes = new ArrayList<>();
    private List<TimeSlotDTO> timeSlots = new ArrayList<>();
    private Long countryID;


    public WTADefaultDataInfoDTO() {
    }

    public Long getCountryID() {
        return countryID;
    }

    public void setCountryID(Long countryID) {
        this.countryID = countryID;
    }

    public List<TimeSlotDTO> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotDTO> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public WTADefaultDataInfoDTO(List<DayTypeDTO> dayTypes, List<PresenceTypeDTO> presenceTypes, List<TimeSlotDTO> timeSlots, Long countryID) {
        this.dayTypes = dayTypes;
        this.presenceTypes = presenceTypes;
        this.timeSlots = timeSlots;
        this.countryID = countryID;
    }


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
