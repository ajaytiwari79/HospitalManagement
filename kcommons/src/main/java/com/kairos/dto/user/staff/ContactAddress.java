package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactAddress{

    private String street1;

    private int floorNumber;

    private String houseNumber;

    private String city;

    private String regionCode;

    private String regionName;

    private String province;

    private float longitude;

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
    private Long zipCode;

    /*@Relationship(type = PAYMENT_TYPE)
    private PaymentType paymentType;

    @Relationship(type = CURRENCY)
    private Currency currency;

    @Relationship(type = ZIP_CODE)
    private ZipCode zipCode;

    @Relationship(type = ADDRESS_ACCESS_DEAILS)
    private AccessToLocation accessToLocation;

    @Relationship(type = MUNICIPALITY)
    private Municipality municipality;*/

    private String description;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public ContactAddress() {
    }

   /* public ContactAddressDTO(String street1, int floorNumber, String houseNumber, ZipCode zipCode) {
        this.street1 = street1;
        this.floorNumber = floorNumber;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
    }*/

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

   /* public AccessToLocation getAccessToLocation() {
        return accessToLocation;
    }

    public void setAccessToLocation(AccessToLocation accessToLocation) {
        this.accessToLocation = accessToLocation;
    }
*/
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

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
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

   /* public ZipCode getZipCode() {
        return zipCode;
    }

    public void setZipCode(ZipCode zipCode) {
        this.zipCode = zipCode;
    }*/

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

  /*  public HousingType getTypeOfHousing() {
        return typeOfHousing;
    }

    public void setTypeOfHousing(HousingType typeOfHousing) {
        this.typeOfHousing = typeOfHousing;
    }*/

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
/*
    public PaymentType getPaymentType() {
        return paymentType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }*/

    public void setContactPersonForBillingAddress(String contactPersonForBillingAddress) {
        this.contactPersonForBillingAddress = contactPersonForBillingAddress;
    }

    public String getContactPersonForBillingAddress() {
        return contactPersonForBillingAddress;
    }


   /* public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public Municipality getMunicipality() {

        return municipality;
    }*/


    public Long getZipCode() {
        return zipCode;
    }

    public void setZipCode(Long zipCode) {
        this.zipCode = zipCode;
    }
}
