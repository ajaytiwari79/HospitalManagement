package com.kairos.dto.planner.vrp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by oodles on 7/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
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

    public TaskAddress(String country, Integer zip, String city, String street, String houseNumber){
        this.country = country;
        this.city = city;
        this.zip = zip;
        this.street = street;
        this.houseNumber = houseNumber;
    }

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

}
