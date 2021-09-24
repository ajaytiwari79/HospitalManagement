package com.kairos.persistence.model.organization.union;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.LOCATION_HAS_ADDRESS;

@NodeEntity
public class Location extends UserBaseEntity {
    private String name;
    private boolean defaultLocation;

    @Relationship(type=LOCATION_HAS_ADDRESS)
    private ContactAddress address;

    public Location() {

    }
    public Location(String name, ContactAddress address) {
        this.name=name;
        this.address = address;
    }

    public Location(String name, boolean defaultLocation, ContactAddress address) {
        this.name = name;
        this.defaultLocation = defaultLocation;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContactAddress getAddress() {
        return address;
    }

    public void setAddress(ContactAddress address) {
        this.address = address;
    }

    public boolean isDefaultLocation() {
        return defaultLocation;
    }

    public void setDefaultLocation(boolean defaultLocation) {
        this.defaultLocation = defaultLocation;
    }
}
