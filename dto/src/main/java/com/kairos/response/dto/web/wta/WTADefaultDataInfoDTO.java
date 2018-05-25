package com.kairos.response.dto.web.wta;

import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.activity.TimeTypeDTO;
import com.kairos.response.dto.web.cta.DayTypeDTO;
import com.kairos.response.dto.web.organization.time_slot.TimeSlotDTO;

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
    private OrganizationDTO organizationDTO;
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

    public WTADefaultDataInfoDTO(List<DayTypeDTO> dayTypes, List<PresenceTypeDTO> presenceTypes,List<TimeSlotDTO> timeSlots,Long countryID) {
        this.dayTypes = dayTypes;
        this.presenceTypes = presenceTypes;
        this.timeSlots = timeSlots;
        this.countryID = countryID;
    }

    public OrganizationDTO getOrganizationDTO() {
        return organizationDTO;
    }

    public void setOrganizationDTO(OrganizationDTO organizationDTO) {
        this.organizationDTO = organizationDTO;
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
