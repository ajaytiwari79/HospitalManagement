package com.kairos.dto.activity.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

/**
 * Created by oodles on 28/9/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactAddressDTO {

    private Long id;

    private String street1;

    private int floorNumber;

    private String houseNumber;

    private String city;

    private String regionCode;

    private String regionName;

    private String province;

    @NotNull(message = "error.ContactAddress.longitude.notnull")
    private float longitude;

    @NotNull(message = "error.ContactAddress.latitude.notnull")
    private float latitude;

    private long startDate;

    private long endDate;

    private String country;

    private boolean privateAddress;

    private boolean isVerifiedByVisitour;

    public boolean isVerifiedByVisitour() {
        return isVerifiedByVisitour;
    }

    public boolean isAddressProtected() {
        return isAddressProtected;
    }

    private boolean isAddressProtected;

    private String streetUrl;

    private boolean isEnabled = true;

    private String contactPersonForBillingAddress;

    private String description;

    //short name of temporary address
    private String locationName;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public ContactAddressDTO() {
    }


    public String getRegionCode() {
        return regionCode;
    }


    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getStartDate() {

        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }



    public Float getLongitude() {
        return longitude;
    }


    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
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


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isPrivateAddress() {
        return privateAddress;
    }

    public void setPrivateAddress(boolean privateAddress) {
        this.privateAddress = privateAddress;
    }

    public void setVerifiedByVisitour(boolean verifiedByVisitour) {
        isVerifiedByVisitour = verifiedByVisitour;
    }

    public void setAddressProtected(boolean addressProtected) {
        isAddressProtected = addressProtected;
    }

    public String getStreetUrl() {
        return streetUrl;
    }

    public void setStreetUrl(String streetUrl) {
        this.streetUrl = streetUrl;
    }


    public void setContactPersonForBillingAddress(String contactPersonForBillingAddress) {
        this.contactPersonForBillingAddress = contactPersonForBillingAddress;
    }

    public String getContactPersonForBillingAddress() {
        return contactPersonForBillingAddress;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

}
