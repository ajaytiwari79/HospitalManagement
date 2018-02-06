package com.kairos.persistence.model.user.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by vipul on 10/11/17.
 */

@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PresenceType extends UserBaseEntity {
    private String name;
    @Relationship(type = BELONGS_TO , direction=Relationship.OUTGOING)
    private Country country;
    private boolean deleted;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public PresenceType() {
    }

    public PresenceType(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "PresenceType{" +
                "name='" + name + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
