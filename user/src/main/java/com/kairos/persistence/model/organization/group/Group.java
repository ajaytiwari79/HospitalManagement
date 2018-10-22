package com.kairos.persistence.model.organization.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.user.skill.Skill;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 6/10/16.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group extends UserBaseEntity {

    @NotBlank(message = "error.name.notnull")
    private String name;
    private boolean isEnabled =true;
    //@NotEmpty(message = "error.description.notnull") @NotNull(message = "error.description.notnull")
    private String description;

    @Relationship(type = HAS_TEAM)
    private List<Team> teamList = new ArrayList<>();


    @Relationship(type = GROUP_HAS_SKILLS)
    private List<Skill> skillList;

    @Relationship(type = GROUP_HAS_SERVICES)
    private List<OrganizationService> serviceList;


    public List<Skill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
    }


    //@prabjot
    //we can't map string as relationship, bothway mapping error will occur
    //Need to fix
    /*@Relationship(type = PROVIDE_TASK_TYPE)
    private List<String> taskTypeList;

    public List<String> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<String> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }*/

    public List<OrganizationService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<OrganizationService> serviceList) {
        this.serviceList = serviceList;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Group(String name, List<Team> teamList) {
        this.name = name;
        this.teamList = teamList;
    }

    public Group() {
    }
}
