package com.kairos.activity.staff;

import com.kairos.persistence.model.country.time_slot.TimeSlotWrapper;
import com.kairos.activity.shift.StaffUnitPositionDetails;
import com.kairos.user.access_group.UserAccessRoleDTO;
import com.kairos.persistence.model.agreement.cta.cta_response.DayTypeDTO;
import org.joda.time.DateTimeZone;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by oodles on 28/11/17.
 */
public class StaffAdditionalInfoDTO {

    private String name;
    private long id;
    private List<Long> teams;
    private List<Long> skills;
    private String profilePic;
    private Long unitId;
    private StaffUnitPositionDetails unitPosition;
    private Date organizationNightStartTimeFrom;
    private Date organizationNightEndTimeTo;
    //These are the all day type of country
    private List<DayTypeDTO> dayTypes;
    private UserAccessRoleDTO user;
    //This is unit TimeZone
    private ZoneId unitTimeZone;
    //these Timeslot is shiftPlanning unit TimeSlot which tells us Day,Evening,Night from to
    private List<TimeSlotWrapper> timeSlotSets;

    public UserAccessRoleDTO getUser() {
        return user;
    }

    public void setUser(UserAccessRoleDTO user) {
        this.user = user;
    }

    public List<TimeSlotWrapper> getTimeSlotSets() {
        return timeSlotSets;
    }

    public void setTimeSlotSets(List<TimeSlotWrapper> timeSlotSets) {
        this.timeSlotSets = timeSlotSets;
    }
    private UserAccessRoleDTO userAccessRoleDTO;


    public DateTimeZone getUnitTimeZone() {
        return unitTimeZone!=null ? DateTimeZone.forID(unitTimeZone.getId()) : null;
    }

    public ZoneId getUnitZoneId() {
        return unitTimeZone;
    }

    public void setUnitTimeZone(ZoneId unitTimeZone) {
        this.unitTimeZone = unitTimeZone;
    }


    public List<DayTypeDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypeDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public StaffAdditionalInfoDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Long> getTeams() {
        return teams;
    }

    public void setTeams(List<Long> teams) {
        this.teams = teams;
    }

    public List<Long> getSkills() {
        return skills;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public StaffUnitPositionDetails getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(StaffUnitPositionDetails unitPosition) {
        this.unitPosition = unitPosition;
    }

    public Date getOrganizationNightStartTimeFrom() {
        return organizationNightStartTimeFrom;
    }

    public void setOrganizationNightStartTimeFrom(Date organizationNightStartTimeFrom) {
        this.organizationNightStartTimeFrom = organizationNightStartTimeFrom;
    }

    public Date getOrganizationNightEndTimeTo() {
        return organizationNightEndTimeTo;
    }

    public void setOrganizationNightEndTimeTo(Date organizationNightEndTimeTo) {
        this.organizationNightEndTimeTo = organizationNightEndTimeTo;
    }


    public UserAccessRoleDTO getUserAccessRoleDTO() {
        return userAccessRoleDTO;
    }

    public void setUserAccessRoleDTO(UserAccessRoleDTO userAccessRoleDTO) {
        this.userAccessRoleDTO = userAccessRoleDTO;
    }
}
