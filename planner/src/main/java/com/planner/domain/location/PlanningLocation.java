package com.planner.domain.location;

import com.planner.domain.common.MongoBaseEntity;
//import org.springframework.data.cassandra.core.mapping.Table;

////@Table
public class PlanningLocation extends MongoBaseEntity {

    private boolean isUnitAddress;
    private String country;
    private int zip;
    private String city;
    private String district;
    private String street;
    private String houseNumber;
    private double longitude;
    private double latitude;


    public boolean isUnitAddress() {

        return isUnitAddress;
    }

    public void setUnitAddress(boolean unitAddress) {
        isUnitAddress = unitAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
