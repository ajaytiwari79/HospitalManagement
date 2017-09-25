package com.kairos.persistence.model.user.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.RelationType;
import org.neo4j.ogm.annotation.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_RELATION_OF;
import static com.kairos.persistence.model.constants.RelationshipConstants.RELATION_TYPE;
import static com.kairos.persistence.model.constants.RelationshipConstants.RELATION_WITH_NEXT_TO_KIN;

/**
 * Created by oodles on 25/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@RelationshipEntity(type = HAS_RELATION_OF)
public class ClientRelationTypeRelationship extends UserBaseEntity {

    @StartNode
    Client nextToKin;
    @EndNode
    RelationType relationType;


    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public Client getNextToKin() {
        return nextToKin;
    }

    public void setNextToKin(Client nextToKin) {
        this.nextToKin = nextToKin;
    }
}
