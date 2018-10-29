package com.kairos.persistence.model.staff.personal_details;

import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.persistence.model.organization.time_slot.TimeSlotWrapper;
import com.kairos.persistence.model.user.unit_position.query_result.StaffUnitPositionDetails;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by prabjot on 17/5/17.
 */
@QueryResult
public class StaffAdditionalInfoQueryResult {

    private String name;
    private long id;
    private List<Long> teams;
    private List<Long> skills;
    private String profilePic;
    private Long unitId;
    private StaffUnitPositionDetails unitPosition;
    private Date organizationNightStartTimeFrom;
    private Date organizationNightEndTimeTo;
    private List<DayTypeDTO> dayTypes;
    private ZoneId unitTimeZone;
    private List<TimeSlotWrapper> timeSlotSets;
    private UserAccessRoleDTO user;
    private UserAccessRoleDTO userAccessRoleDTO;
    private Long staffUserId;

    public Long getStaffUserId() {
        return staffUserId;
    }

    public void setStaffUserId(Long staffUserId) {
        this.staffUserId = staffUserId;
    }

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

    public ZoneId getUnitTimeZone() {
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

    public StaffAdditionalInfoQueryResult() {
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfilePic() {

        return profilePic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTeams(List<Long> teams) {
        this.teams = teams;
    }

    public void setSkills(List<Long> skills) {
        this.skills = skills;
    }

    public String getName() {

        return name;
    }

    public long getId() {
        return id;
    }

    public List<Long> getTeams() {
        return Optional.ofNullable(teams).orElse(new ArrayList<>());
    }

    public List<Long> getSkills() {
        return Optional.ofNullable(skills).orElse(new ArrayList<>());
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
