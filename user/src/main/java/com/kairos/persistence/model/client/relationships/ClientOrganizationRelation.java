package com.kairos.persistence.model.client.relationships;
import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
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
    Organization organization;

    private Long joinDate;
    private String employmentId = UUID.randomUUID().toString().toUpperCase();


    public ClientOrganizationRelation() {
    }

    public ClientOrganizationRelation(Client client, Organization organization, Long joinDate) {
        this.client = client;
        this.organization = organization;
        this.joinDate = joinDate;

    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
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
