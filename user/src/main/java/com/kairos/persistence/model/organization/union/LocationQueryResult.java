package com.kairos.persistence.model.organization.union;

import com.kairos.persistence.model.client.ContactAddress;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class LocationQueryResult {

    private Location location;
    private Long addressId;
    private Long zipCodeId;
    private Long municipalityId;
    private Long unionId;
    private ContactAddress address;


    public Long getZipCodeId() {
        return zipCodeId;
    }

    public void setZipCodeId(Long zipCodeId) {
        this.zipCodeId = zipCodeId;
    }

    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getUnionId() {
        return unionId;
    }

    public void setUnionId(Long unionId) {
        this.unionId = unionId;
    }

    public ContactAddress getAddress() {
        return address;
    }

    public void setAddress(ContactAddress address) {
        this.address = address;
    }
}
