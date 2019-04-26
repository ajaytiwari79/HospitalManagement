package com.kairos.persistence.model.user.department;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.personal_details.Staff;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_STAFF;
import static com.kairos.persistence.model.constants.RelationshipConstants.MANAGE;


/**
 * Created by prabjot on 9/20/16.
 */
@NodeEntity
public class Department extends UserBaseEntity {

    private String name;

    @Relationship(type = HAS_STAFF)
    private List<Staff> teams;

    @Relationship(type = MANAGE)
    private List<Organization> organizations;

    public Department(String s) {
        this.name = s;

    }

    public void setName(String name) {
        this.name = name;

    }
    public String getName() {
        return name;

    }

    public List<Staff> getTeams() {
        return teams;
    }

    public void setTeams(List<Staff> teams) {
        this.teams = teams;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public Department() {
    }

    public Department(String name, List<Staff> teams) {
        this.name = name;
        this.teams = teams;
    }
}

