package com.kairos.persistence.model.user.country;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.constants.RelationshipConstants;
import com.kairos.persistence.model.enums.OrganizationCategory;
import com.kairos.persistence.model.user.access_permission.AccessPage;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 27/2/18.
 */
@RelationshipEntity(type = RelationshipConstants.HAS_ACCESS_FOR_ORG_CATEGORY)
public class CountryAccessGroupRelationship extends UserBaseEntity {

    @StartNode
    private Country country;

    @EndNode
    private AccessPage accessPage;

    private List<OrganizationCategory> accessibleFor = new ArrayList<>();

    public CountryAccessGroupRelationship(){
        // default constructor
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public AccessPage getAccessPage() {
        return accessPage;
    }

    public void setAccessPage(AccessPage accessPage) {
        this.accessPage = accessPage;
    }

    public List<OrganizationCategory> getAccessibleFor() {
        return accessibleFor;
    }

    public void setAccessibleFor(List<OrganizationCategory> accessibleFor) {
        this.accessibleFor = accessibleFor;
    }
}
