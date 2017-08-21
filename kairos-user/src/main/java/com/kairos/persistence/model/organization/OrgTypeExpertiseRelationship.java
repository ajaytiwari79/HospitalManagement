package com.kairos.persistence.model.organization;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import com.kairos.persistence.model.user.expertise.Expertise;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 12/4/17.
 */
@RelationshipEntity(type = ORG_TYPE_HAS_EXPERTISE)
public class OrgTypeExpertiseRelationship {

    @StartNode private OrganizationType organizationType;
    @EndNode private Expertise expertise;
    private boolean isEnabled = true;

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
