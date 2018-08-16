package com.kairos.persistence.model.client.relationships;

import com.kairos.persistence.model.client.Client;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * Created by oodles on 4/10/16.
 */
@RelationshipEntity(type = "IS_RELATIVE_OF")
public class ClientRelativeRelation extends UserBaseEntity {
    @StartNode
    Client client;

    @EndNode
    Client relative;

    private String priority;
    private String distanceToRelative;
    private boolean canUpdateOnPublicPortal;
    private boolean isFullGuardian;
    private String remarks;
    private String relation;
    private boolean isNextToKin;

    public ClientRelativeRelation(Client client, Client relative, String priority, boolean canUpdateOnPublicPortal, boolean isFullGuardian, boolean nextToKin, String relation) {
        this.client = client;
        this.relative = relative;
        this.priority = priority;
        this.canUpdateOnPublicPortal = canUpdateOnPublicPortal;
        this.isFullGuardian = isFullGuardian;
        this.isNextToKin = nextToKin;
        this.relation = relation;


    }

    public ClientRelativeRelation() {

    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public boolean isNextToKin() {
        return isNextToKin;
    }

    public void setNextToKin(boolean nextToKin) {
        isNextToKin = nextToKin;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getRelative() {
        return relative;
    }

    public void setRelative(Client relative) {
        this.relative = relative;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDistanceToRelative() {
        return distanceToRelative;
    }

    public void setDistanceToRelative(String distanceToRelative) {
        this.distanceToRelative = distanceToRelative;
    }

    public boolean isCanUpdateOnPublicPortal() {
        return canUpdateOnPublicPortal;
    }

    public void setCanUpdateOnPublicPortal(boolean canUpdateOnPublicPortal) {
        this.canUpdateOnPublicPortal = canUpdateOnPublicPortal;
    }

    public boolean isFullGuardian() {
        return isFullGuardian;
    }

    public void setFullGuardian(boolean fullGuardian) {
        isFullGuardian = fullGuardian;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
