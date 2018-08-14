package com.kairos.persistence.model.country;

/**
 * Created by prerna on 4/3/18.
 */

import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.constants.RelationshipConstants;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.EnumString;

import java.util.ArrayList;
import java.util.List;

@RelationshipEntity(type = RelationshipConstants.HAS_ACCESS_GROUP)
public class CountryAccessGroupRelationship extends UserBaseEntity{

    @StartNode
    private Country country;

    @EndNode
    private AccessGroup accessGroup;

    @Property(name = "organizationCategory")
    @EnumString(OrganizationCategory.class)
    private OrganizationCategory organizationCategory;

    public CountryAccessGroupRelationship(){
        // default constructor
    }

    public CountryAccessGroupRelationship(Country country, AccessGroup accessGroup, OrganizationCategory category){
        this.country = country;
        this.accessGroup = accessGroup;
        this.organizationCategory = category;
    }


    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;
    }

    public OrganizationCategory getOrganizationCategory() {
        return organizationCategory;
    }

    public void setOrganizationCategory(OrganizationCategory organizationCategory) {
        this.organizationCategory = organizationCategory;
    }

}
