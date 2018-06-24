package com.kairos.persistence.model.user.client;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.auth.User;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * @author pradeep
 * @date - 11/6/18
 */

public class VRPClient extends User {

    private Long installationNumber;
    private Double latitude;
    private Double longitude;
    private int duration;
    private String streetName;
    private int houseNumber;
    private String block;
    private int floorNumber;
    private int zipCode;
    private String city;

    @Relationship(type = BELONGS_TO)
    private Organization organization;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getInstallationNumber() {
        return installationNumber;
    }

    public void setInstallationNumber(Long installationNumber) {
        this.installationNumber = installationNumber;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
