package com.kairos.persistence.model.user.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.country.Currency;
import com.kairos.persistence.model.user.country.HousingType;
import com.kairos.persistence.model.user.payment_type.PaymentType;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 28/9/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class ContactAddress extends UserBaseEntity implements Cloneable{

    private String street1;

    private int floorNumber;

    private String houseNumber;

    private String city;

    private String regionCode;

    private String regionName;

    private String province;

    @Relationship(type = TYPE_OF_HOUSING)
    private HousingType typeOfHousing;

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

    @Relationship(type = PAYMENT_TYPE)
    private PaymentType paymentType;

    @Relationship(type = CURRENCY)
    private Currency currency;

    @Relationship(type = ZIP_CODE)
    private ZipCode zipCode;

    @Relationship(type = ADDRESS_ACCESS_DEAILS)
    private AccessToLocation accessToLocation;

    @Relationship(type = MUNICIPALITY)
    private Municipality municipality;

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

    public ContactAddress() {
    }

    public ContactAddress(String street1, int floorNumber,String houseNumber, ZipCode zipCode) {
        this.street1 = street1;
        this.floorNumber = floorNumber;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccessToLocation getAccessToLocation() {
        return accessToLocation;
    }

    public void setAccessToLocation(AccessToLocation accessToLocation) {
        this.accessToLocation = accessToLocation;
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

    public ZipCode getZipCode() {
        return zipCode;
    }

    public void setZipCode(ZipCode zipCode) {
        this.zipCode = zipCode;
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

    public HousingType getTypeOfHousing() {
        return typeOfHousing;
    }

    public void setTypeOfHousing(HousingType typeOfHousing) {
        this.typeOfHousing = typeOfHousing;
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

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public Municipality getMunicipality() {

        return municipality;
    }

    /**
     * @autor prabjot
     * static factory to get instance of contact address, this is mainly used for client address,
     * whenever new client address will be created,default access to location also will be created
     * @return
     */
    public static ContactAddress getInstance(){
        ContactAddress contactAddress = new ContactAddress();
        contactAddress.setAccessToLocation(new AccessToLocation());
        return contactAddress;
    }

    public  static ContactAddress copyProperties(ContactAddress source, ContactAddress target){
        BeanUtils.copyProperties(source, target);
        target.setId(null);
        return target;
    }
}
