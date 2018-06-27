package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prabjot on 21/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class AddressQueryResult {

    private Long id;
    private String street1;
    private int floorNumber;
    private String houseNumber;
    private String city;
    private Float longitude;
    private Float latitude;
    private Long zipCodeId;
    private Long municipalityId;
    private boolean verifiedByGoogleMap;
    private Integer zipCodeValue;

    public Integer getZipCodeValue() {
        return zipCodeValue;
    }

    public void setZipCodeValue(Integer zipCodeValue) {
        this.zipCodeValue = zipCodeValue;
    }

    public boolean isVerifiedByGoogleMap() {
        return verifiedByGoogleMap;
    }

    public void setVerifiedByGoogleMap(boolean verifiedByGoogleMap) {
        this.verifiedByGoogleMap = verifiedByGoogleMap;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

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
}
