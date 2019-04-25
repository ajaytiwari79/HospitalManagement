package com.kairos.persistence.model.organization;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * Created by oodles on 22/9/16.
 */

@RelationshipEntity(type = "ORGANIZATION_HAS_CHILD")
public class OrganizationRelationship{

    @StartNode
    private Organization parentOrganization;

    @EndNode
    private Organization childOrganization;

    private String childLevelName;

    public OrganizationRelationship() {
        //Default Constructor
    }


    public Organization getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(Organization parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public Organization getChildOrganization() {
        return childOrganization;
    }

    public void setChildOrganization(Organization childOrganization) {
        this.childOrganization = childOrganization;
    }

    public String getChildLevelName() {
        return childLevelName;
    }

    public void setChildLevelName(String childLevelName) {
        this.childLevelName = childLevelName;
    }
}
