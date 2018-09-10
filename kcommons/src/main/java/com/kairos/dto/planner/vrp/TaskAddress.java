package com.kairos.dto.planner.vrp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by oodles on 7/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskAddress {

    private String country;
    private Integer zip;
    private String city;
    private String district;
    private String street;
    private String houseNumber;
    private String longitude;
    private String latitude;
    private String block;
    private int floorNo;


    public TaskAddress(Integer zip, String city, String street, String houseNumber, String longitude, String latitude, String block, int floorNo) {
        this.zip = zip;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.longitude = longitude;
        this.latitude = latitude;
        this.block = block;
        this.floorNo = floorNo;
    }

    public TaskAddress() {
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
