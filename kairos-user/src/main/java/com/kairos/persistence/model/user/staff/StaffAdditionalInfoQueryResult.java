package com.kairos.persistence.model.user.staff;

import com.kairos.persistence.model.user.unitEmploymentPosition.StaffUnitEmploymentDetails;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalTime;
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
    private StaffUnitEmploymentDetails unitEmploymentPosition;
    private Date organizationNightStartTimeFrom;
    private Date organizationNightEndTimeTo;

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

    public StaffUnitEmploymentDetails getUnitEmploymentPosition() {
        return unitEmploymentPosition;
    }

    public void setUnitEmploymentPosition(StaffUnitEmploymentDetails unitEmploymentPosition) {
        this.unitEmploymentPosition = unitEmploymentPosition;
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
