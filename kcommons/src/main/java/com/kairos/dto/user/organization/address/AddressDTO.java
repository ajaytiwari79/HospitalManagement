package com.kairos.dto.user.organization.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by prabjot on 19/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO {
    private Long id;
    private String streetUrl;
    private String street1;
    private int floorNumber;
    private String houseNumber;
    private String billingPerson;
    private String city;
    private String municipalityName;
    private float longitude;
    private float latitude;
    private Long zipCode;

    private Long typeOfHouseId;

    private long paymentTypeId;
    private long currencyId;
    private String country;
    private String regionName;
    private String province;
    private boolean isVerifiedByGoogleMap;
    private boolean addressProtected;
    private boolean verifiedByVisitour;
    private long startDate;
    private long endDate;
    private String description;
    private Long municipalityId;



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Long getTypeOfHouseId() {
        return typeOfHouseId;
    }

    public void setTypeOfHouseId(Long typeOfHouseId) {
        this.typeOfHouseId = typeOfHouseId;
    }

    public boolean isAddressProtected() {
        return addressProtected;
    }

    public void setVerifiedByGoogleMap(boolean verifiedByGoogleMap) {
        isVerifiedByGoogleMap = verifiedByGoogleMap;
    }

    public boolean isVerifiedByVisitour() {
        return verifiedByVisitour;
    }

    public void setAddressProtected(boolean addressProtected) {
        this.addressProtected = addressProtected;
    }

    public void setVerifiedByVisitour(boolean verifiedByVisitour) {
        this.verifiedByVisitour = verifiedByVisitour;
    }

    public boolean isVerifiedByGoogleMap() {
        return isVerifiedByGoogleMap;
    }

    public void setIsVerifiedByGoogleMap(boolean verifiedByGoogleMap) {
        isVerifiedByGoogleMap = verifiedByGoogleMap;
    }

    public String getStreetUrl() {
        return streetUrl;
    }

    public long getCurrencyId() {
        return currencyId;
    }

    public String getProvince() {
        return province;
    }

    public String getStreet1() {
        return street1;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getCity() {
        return city;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public float getLatitude() {
        return latitude;
    }

    public long getPaymentTypeId() {
        return paymentTypeId;
    }

    public String getCountry() {
        return country;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setStreetUrl(String streetUrl) {
        this.streetUrl = streetUrl;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public void setPaymentTypeId(long paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public void setCurrencyId(long currencyId) {
        this.currencyId = currencyId;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getZipCode() {
        return zipCode;
    }

    public void setZipCode(Long zipCode) {
        this.zipCode = zipCode;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getBillingPerson() {
        return billingPerson;
    }

    public void setBillingPerson(String billingPerson) {
        this.billingPerson = billingPerson;
    }

    public void setMunicipalityId(Long municipalityId) {
        this.municipalityId = municipalityId;
    }

    public Long getMunicipalityId() {

        return municipalityId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
