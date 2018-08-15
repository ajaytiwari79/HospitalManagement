package com.kairos.persistence.model.client.relationships;

import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.NEXT_TO_KIN;

/**
 * Created by prabjot on 18/9/17.
 */
@RelationshipEntity(type = NEXT_TO_KIN)
public class ClientNextToKinRelationship extends UserBaseEntity {

    @StartNode
    private Client client;
    @EndNode
    private Client nextToKin;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getNextToKin() {
        return nextToKin;
    }

    public void setNextToKin(Client nextToKin) {
        this.nextToKin = nextToKin;
    }
}
