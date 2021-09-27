package com.kairos.persistence.model.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.annotations.KPermissionField;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.default_data.Currency;
import com.kairos.persistence.model.country.default_data.HousingType;
import com.kairos.persistence.model.country.default_data.PaymentType;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotNull;

import static com.kairos.constants.UserMessagesConstants.ERROR_CONTACTADDRESS_LATITUDE_NOTNULL;
import static com.kairos.constants.UserMessagesConstants.ERROR_CONTACTADDRESS_LONGITUDE_NOTNULL;
import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 28/9/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
@Getter
@Setter
@NoArgsConstructor
public class ContactAddress extends UserBaseEntity{
    private static final long serialVersionUID = 3513986749867681647L;
    @KPermissionField
    private String street;
    @KPermissionField
    private int floorNumber;
    @KPermissionField
    private String houseNumber;
    @KPermissionField
    private String city;
    @KPermissionField
    private String regionCode;

    private String regionName;
    @KPermissionField
    private String province;

    @Relationship(type = TYPE_OF_HOUSING)
    private HousingType typeOfHousing;

    @NotNull(message = ERROR_CONTACTADDRESS_LONGITUDE_NOTNULL)
    private float longitude;

    @NotNull(message = ERROR_CONTACTADDRESS_LATITUDE_NOTNULL)
    private float latitude;

    private long startDate;

    private long endDate;

    @KPermissionField
    private String country;

    private boolean privateAddress;

    private boolean verifiedByVisitour;

    @KPermissionField
    private boolean addressProtected;

    @KPermissionField
    private String streetUrl;

    private boolean isEnabled = true;

    private String contactPersonForBillingAddress;

    @Relationship(type = PAYMENT_TYPE)
    private PaymentType paymentType;

    @Relationship(type = CURRENCY)
    private Currency currency;
    @KPermissionField
    @Relationship(type = ZIP_CODE)
    private ZipCode zipCode;

    @Relationship(type = ADDRESS_ACCESS_DEAILS)
    private AccessToLocation accessToLocation;

    @KPermissionField
    @Relationship(type = MUNICIPALITY)
    private Municipality municipality;

    private String description;

    //short name of temporary address
    private String locationName;
    private boolean primary;

    public ContactAddress(String houseNumber,String province,String street,String city,String regionName) {

        this.houseNumber = houseNumber;
        this.province = province;
        this.street = street;
        this.city = city;
        this.regionName = regionName;
    }


    public ContactAddress(Municipality municipality,@NotNull(message = ERROR_CONTACTADDRESS_LONGITUDE_NOTNULL) float longitude,@NotNull(message = ERROR_CONTACTADDRESS_LATITUDE_NOTNULL) float latitude,String province, String regionName,String city,String country,ZipCode zipCode,String houseNumber,String street, String streetUrl,int floorNumber) {
        this.municipality = municipality;
        this.street = street;
        this.floorNumber = floorNumber;
        this.city = city;
        this.regionName = regionName;
        this.province = province;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.streetUrl = streetUrl;
        this.zipCode = zipCode;

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



}
