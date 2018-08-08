package com.kairos.persistence.model.client.relationships;

import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.client.ClientContactPerson;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.CLIENT_CONTACT_PERSON_RELATION_TYPE;


/**
 * Created by Jasgeet on 4/10/17.
 */
@RelationshipEntity(type = CLIENT_CONTACT_PERSON_RELATION_TYPE)
public class ClientContactPersonRelationship  extends UserBaseEntity {

    @StartNode
    private Client client;

    @EndNode
    private ClientContactPerson clientContactPerson;

    private ContactPersonRelationType contactPersonRelationType;

    public ContactPersonRelationType getContactPersonRelationType() {
        return contactPersonRelationType;
    }

    public void setContactPersonRelationType(ContactPersonRelationType contactPersonRelationType) {
        this.contactPersonRelationType = contactPersonRelationType;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientContactPerson getClientContactPerson() {
        return clientContactPerson;
    }

    public void setClientContactPerson(ClientContactPerson clientContactPerson) {
        this.clientContactPerson = clientContactPerson;
    }

    public enum ContactPersonRelationType {

        PRIMARY,SECONDARY_ONE,SECONDARY_TWO,SECONDARY_THREE;
    }
}
