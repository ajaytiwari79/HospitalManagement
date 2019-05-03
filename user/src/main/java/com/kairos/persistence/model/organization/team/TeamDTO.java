package com.kairos.persistence.model.organization.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;

/**
 * Created by prabjot on 20/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class TeamDTO {

    private Long id;
    @NotBlank(message = "error.name.notnull")
    private String name;
    private String description;
    private boolean hasAddressOfUnit;
    private Long teamLeaderStaffId; //Id of Staff who is assigned as team leader
    private List<BigInteger> activityIds;
    private List<Long> teamMemberIds;
    private List<Long> skillIds;
    private Set<Long> mainTeamLeaderIds;
    private Set<Long> actingTeamLeaderIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public boolean isHasAddressOfUnit() {
        return hasAddressOfUnit;
    }

    public void setHasAddressOfUnit(boolean hasAddressOfUnit) {
        this.hasAddressOfUnit = hasAddressOfUnit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTeamLeaderStaffId() {
        return teamLeaderStaffId;
    }

    public void setTeamLeaderStaffId(Long teamLeaderStaffId) {
        this.teamLeaderStaffId = teamLeaderStaffId;
    }

    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }

    public List<Long> getTeamMemberIds() {
        return teamMemberIds;
    }

    public void setTeamMemberIds(List<Long> teamMemberIds) {
        this.teamMemberIds = teamMemberIds;
    }

    public List<Long> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds;
    }

    public Set<Long> getMainTeamLeaderIds() {
        return mainTeamLeaderIds;
    }

    public void setMainTeamLeaderIds(Set<Long> mainTeamLeaderIds) {
        this.mainTeamLeaderIds = mainTeamLeaderIds;
    }

    public Set<Long> getActingTeamLeaderIds() {
        return actingTeamLeaderIds;
    }

    public void setActingTeamLeaderIds(Set<Long> actingTeamLeaderIds) {
        this.actingTeamLeaderIds = actingTeamLeaderIds;
    }

    @AssertTrue(message = "Please provide a valid user name")
    public boolean isValid() {
        if(isCollectionEmpty(mainTeamLeaderIds) || isCollectionEmpty(actingTeamLeaderIds)){
            return true;
        }



    }


}
