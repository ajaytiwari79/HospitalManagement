package com.kairos.persistence.model.client;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.staff.Staff;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.CLIENT_CONTACT_PERSON_SERVICE;
import static com.kairos.persistence.model.constants.RelationshipConstants.CLIENT_CONTACT_PERSON_STAFF;

/**
 * Created by Jasgeet on 4/10/17.
 */
public class ClientContactPerson extends UserBaseEntity {

    @Relationship(type = CLIENT_CONTACT_PERSON_STAFF)
    private Staff staff;

    @Relationship(type = CLIENT_CONTACT_PERSON_SERVICE)
    private OrganizationService organizationService;

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }
}
