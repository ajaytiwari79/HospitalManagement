package com.kairos.persistence.model.client.relationships;

import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.UUID;

import static com.kairos.persistence.model.constants.RelationshipConstants.GET_SERVICE_FROM;


/**
 * Created by oodles on 4/10/16.
 */
@RelationshipEntity(type = GET_SERVICE_FROM)
public class ClientOrganizationRelation extends UserBaseEntity {

    @StartNode
    Client client;

    @EndNode
    Unit unit;

    private Long joinDate;
    private String employmentId = UUID.randomUUID().toString().toUpperCase();


    public ClientOrganizationRelation() {
    }

    public ClientOrganizationRelation(Client client, Unit unit, Long joinDate) {
        this.client = client;
        this.unit = unit;
        this.joinDate = joinDate;

    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Long getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Long joinDate) {
        this.joinDate = joinDate;
    }

    public String getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(String employmentId) {
        this.employmentId = employmentId;
    }
}
