package com.kairos.persistence.model.organization.team;


/**
 * Created by prabjot on 16/2/17.
 */
public class TeamAddressDTO {

    private String streetViewUrl;
    private String street1;
    private int floorNumber;
    private String houseNumber;
    private String city;
    private String municipalityName;
    private Float longitude;
    private Float latitude;
    private Long zipCodeId;
    private int zipCodeValue;
    private String zipCodeName;
    private String country;
    private String regionName;
    private String province;
    private boolean isVerifiedByGoogleMap;
    private boolean addressProtected;
    private boolean verifiedByVisitour;

    public String getStreetViewUrl() {
        return streetViewUrl;
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

    public Float getLongitude() {
        return longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Long getZipCodeId() {
        return zipCodeId;
    }

    public int getZipCodeValue() {
        return zipCodeValue;
    }

    public String getZipCodeName() {
        return zipCodeName;
    }

    public String getCountry() {
        return country;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getProvince() {
        return province;
    }

    public boolean isVerifiedByGoogleMap() {
        return isVerifiedByGoogleMap;
    }

    public boolean isAddressProtected() {
        return addressProtected;
    }

    public boolean isVerifiedByVisitour() {
        return verifiedByVisitour;
    }
}
