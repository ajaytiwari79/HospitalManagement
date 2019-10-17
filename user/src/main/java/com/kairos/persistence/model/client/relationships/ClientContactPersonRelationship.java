package com.kairos.persistence.model.client.relationships;

import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.client.ClientContactPerson;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.CLIENT_CONTACT_PERSON_RELATION_TYPE;


/**
 * Created by Jasgeet on 4/10/17.
 */
@Getter
@Setter
@NoArgsConstructor
@RelationshipEntity(type = CLIENT_CONTACT_PERSON_RELATION_TYPE)
public class ClientContactPersonRelationship  extends UserBaseEntity {

    @StartNode
    private Client client;

    @EndNode
    private ClientContactPerson clientContactPerson;

    private ContactPersonRelationType contactPersonRelationType;

    public ClientContactPersonRelationship(Client client, ClientContactPerson clientContactPerson, ContactPersonRelationType contactPersonRelationType){
        this.client = client;
        this.clientContactPerson = clientContactPerson;
        this.contactPersonRelationType = contactPersonRelationType;
    }

    public enum ContactPersonRelationType {

        PRIMARY,SECONDARY_ONE,SECONDARY_TWO,SECONDARY_THREE;
    }
}
