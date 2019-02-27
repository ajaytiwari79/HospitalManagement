package com.kairos.dto.user.staff.client;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by pavan on 27/2/18.
 */
public class ContactAddressDTO {
    @NotBlank(message = "message.houseNumber.null")
    private String houseNumber;
    private Integer floorNumber;
    @NotBlank(message = "message.street.null")
    private String street;
    @NotNull(message = "message.zipCodeId.null")
    private Long zipCodeId;
    @NotBlank(message = "message.city.null")
    private String city;
    @NotNull(message = "message.municipality.null")
    private Long municipalityId;
    @NotBlank(message = "message.region.null")
    private String regionName;
    private String country;
    private Float latitude;
    private Float longitude;
    @NotBlank(message = "message.province.null")
    private String province;
    private String streetUrl;
    private Boolean addressProtected;
    private Boolean verifiedByVisitour;
    private Integer zipCodeValue;
    private String municipalityName;

    public ContactAddressDTO() {
    }

    public ContactAddressDTO(String houseNumber, Integer floorNumber, String street, String city, String regionName, String country, Float latitude, Float longitude,
                             String province, String streetUrl, Boolean addressProtected, Boolean verifiedByVisitour) {
        this.houseNumber = houseNumber;
        this.floorNumber = floorNumber;
        this.street = street;
        this.city = city;
        this.regionName = regionName;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
        this.streetUrl = streetUrl;
        this.addressProtected = addressProtected;
        this.verifiedByVisitour = verifiedByVisitour;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = StringUtils.trim(houseNumber);
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = StringUtils.trim(street);
    }

    public Long getZipCodeId() {
        return zipCodeId;
    }

    public void setZipCodeId(Long zipCodeId) {
        this.zipCodeId = zipCodeId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = StringUtils.trim(city);
    }

    public Long getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = StringUtils.trim(regionName);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = StringUtils.trim(country);
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = StringUtils.trim(province);
    }

    public String getStreetUrl() {
        return streetUrl;
    }

    public void setStreetUrl(String streetUrl) {
        this.streetUrl = StringUtils.trim(streetUrl);
    }

    public Boolean getAddressProtected() {
        return addressProtected;
    }

    public void setAddressProtected(Boolean addressProtected) {
        this.addressProtected = addressProtected;
    }

    public Boolean getVerifiedByVisitour() {
        return verifiedByVisitour;
    }

    public void setVerifiedByVisitour(Boolean verifiedByVisitour) {
        this.verifiedByVisitour = verifiedByVisitour;
    }

    public Integer getZipCodeValue() {
        return zipCodeValue;
    }

    public void setZipCodeValue(Integer zipCodeValue) {
        this.zipCodeValue = zipCodeValue;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = StringUtils.trim(municipalityName);
    }
}


