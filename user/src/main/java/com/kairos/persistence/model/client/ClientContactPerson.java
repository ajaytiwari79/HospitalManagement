package com.kairos.persistence.model.client;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.staff.personal_details.Staff;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.CLIENT_CONTACT_PERSON_SERVICE;
import static com.kairos.persistence.model.constants.RelationshipConstants.CLIENT_CONTACT_PERSON_STAFF;

/**
 * Created by Jasgeet on 4/10/17.
 */
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class ClientContactPerson extends UserBaseEntity {

    @Relationship(type = CLIENT_CONTACT_PERSON_STAFF)
    private Staff staff;

    @Relationship(type = CLIENT_CONTACT_PERSON_SERVICE)
    private OrganizationService organizationService;

    public ClientContactPerson(Staff staff, OrganizationService organizationService){
        this.staff = staff;
        this.organizationService = organizationService;
    }
}
