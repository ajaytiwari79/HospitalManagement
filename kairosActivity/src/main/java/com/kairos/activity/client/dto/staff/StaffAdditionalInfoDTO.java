package com.kairos.activity.client.dto.staff;

import com.kairos.activity.persistence.model.task_demand.MonthlyFrequency;
import com.kairos.activity.response.dto.shift.StaffUnitPositionDetails;
import org.joda.time.DateTimeZone;

import java.time.DayOfWeek;
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
    private List<DayOfWeek> activityDayTypes;
    private ZoneId unitTimeZone;


    public DateTimeZone getUnitTimeZone() {
        return DateTimeZone.forID(unitTimeZone.getId());
    }

    public ZoneId getUnitZoneId() {
        return unitTimeZone;
    }

    public void setUnitTimeZone(ZoneId unitTimeZone) {
        this.unitTimeZone = unitTimeZone;
    }

    public List<DayOfWeek> getActivityDayTypes() {
        return activityDayTypes;
    }

    public void setActivityDayTypes(List<DayOfWeek> activityDayTypes) {
        this.activityDayTypes = activityDayTypes;
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
}
