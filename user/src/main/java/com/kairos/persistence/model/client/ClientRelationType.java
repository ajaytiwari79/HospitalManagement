package com.kairos.persistence.model.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.default_data.RelationType;
import org.neo4j.ogm.annotation.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.RELATION_TYPE;
import static com.kairos.persistence.model.constants.RelationshipConstants.RELATION_WITH_NEXT_TO_KIN;

/**
 * Created by oodles on 25/9/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class ClientRelationType extends UserBaseEntity {

    @Relationship(type = RELATION_WITH_NEXT_TO_KIN)
    Client nextToKin;
    @Relationship(type = RELATION_TYPE)
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
