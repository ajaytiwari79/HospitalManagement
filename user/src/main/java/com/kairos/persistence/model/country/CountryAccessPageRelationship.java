package com.kairos.persistence.model.country;

import com.kairos.persistence.model.access_permission.AccessPage;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.constants.RelationshipConstants;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * Created by prerna on 27/2/18.
 */
@RelationshipEntity(type = RelationshipConstants.HAS_ACCESS_FOR_ORG_CATEGORY)
public class CountryAccessPageRelationship extends UserBaseEntity {

    @StartNode
    private Country country;

    @EndNode
    private AccessPage accessPage;

    private boolean accessibleForHub;
    private boolean accessibleForUnion;
    private boolean accessibleForOrganization;

    /*private List<OrganizationCategory> accessibleFor = new ArrayList<>();*/

    public CountryAccessPageRelationship(){
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

    public boolean isAccessibleForHub() {
        return accessibleForHub;
    }

    public void setAccessibleForHub(boolean accessibleForHub) {
        this.accessibleForHub = accessibleForHub;
    }

    public boolean isAccessibleForUnion() {
        return accessibleForUnion;
    }

    public void setAccessibleForUnion(boolean accessibleForUnion) {
        this.accessibleForUnion = accessibleForUnion;
    }

    public boolean isAccessibleForOrganization() {
        return accessibleForOrganization;
    }

    public void setAccessibleForOrganization(boolean accessibleForOrganization) {
        this.accessibleForOrganization = accessibleForOrganization;
    }

    /*public List<OrganizationCategory> getAccessibleFor() {
        return accessibleFor;
    }

    public void setAccessibleFor(List<OrganizationCategory> accessibleFor) {
        this.accessibleFor = accessibleFor;
    }*/
}
