package com.kairos.persistence.model.organization.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.user.skill.Skill;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.PROVIDE_TASK_TYPE;
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

    private String visitourId;

    private String description;

    /**
     * Specifies if team's address is same as unit's address
     */
    private boolean hasAddressOfUnit;

    @Relationship(type = TEAM_HAS_SKILLS)
    private List<Skill> skillList;

    @Relationship(type = TEAM_HAS_LOCATION)
    private ContactAddress contactAddress;

    //@Relationship(type = PROVIDE_TASK_TYPE)
    private List<String> taskTypeList;

    private boolean isEnabled = true;


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

    public List<String> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<String> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public void setContactAddress(ContactAddress contactAddress) {
        this.contactAddress = contactAddress;
    }

    public ContactAddress getContactAddress() {
        return contactAddress;
    }

    public String getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(String visitourId) {
        this.visitourId = visitourId;
    }

    public boolean isHasAddressOfUnit() {
        return hasAddressOfUnit;
    }

    public void setHasAddressOfUnit(boolean hasAddressOfUnit) {
        this.hasAddressOfUnit = hasAddressOfUnit;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Team() {
    }

    public Team(String name,boolean hasAddressOfUnit) {
        this.name = name;
        this.hasAddressOfUnit = hasAddressOfUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
