package com.kairos.persistence.model.user.client;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.PEOPLE_IN_HOUSEHOLD_LIST;


/**
 * Created by prabjot on 18/5/17.
 */
@RelationshipEntity(type = PEOPLE_IN_HOUSEHOLD_LIST)
public class HouseHoldPeopleRelationship extends UserBaseEntity {

    @StartNode private Client client;
    @EndNode private Client peopleInHouseHold;

    public void setPeopleInHouseHold(Client peopleInHouseHold) {
        this.peopleInHouseHold = peopleInHouseHold;
    }

    public Client getPeopleInHouseHold() {

        return peopleInHouseHold;
    }

    public void setClient(Client client) {

        this.client = client;
    }

    public Client getClient() {

        return client;
    }
}
