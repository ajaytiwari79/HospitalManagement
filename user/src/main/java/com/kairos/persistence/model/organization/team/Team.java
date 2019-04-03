package com.kairos.persistence.model.organization.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.skill.Skill;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_LOCATION;
import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_SKILLS;


/**
 * Created by prabjot on 9/20/16.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team extends UserBaseEntity {

    private String name;
    private String description;
    /**
     * Specifies if team's address is same as unit's address
     */

    @Relationship(type = TEAM_HAS_SKILLS)
    private List<Skill> skillList;

    @Relationship(type = TEAM_HAS_LOCATION)
    private ContactAddress contactAddress;

    private boolean isEnabled = true;
    private Set<BigInteger> activityIds;
    Long teamLeaderStaffId; //Id of Staff who is assigned as team leader

    public Team() {
    }

    public Team(String name, String description,  ContactAddress contactAddress) {
        this.name = name;
        this.description = description;
        this.contactAddress = contactAddress;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactAddress(ContactAddress contactAddress) {
        this.contactAddress = contactAddress;
    }

    public ContactAddress getContactAddress() {
        return contactAddress;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Set<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(Set<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }

    public Long getTeamLeaderStaffId() {
        return teamLeaderStaffId;
    }

    public void setTeamLeaderStaffId(Long teamLeaderStaffId) {
        this.teamLeaderStaffId = teamLeaderStaffId;
    }
}
