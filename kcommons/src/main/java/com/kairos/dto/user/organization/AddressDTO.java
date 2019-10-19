package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by prabjot on 19/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class AddressDTO {
    private Long id;
    private String streetUrl;
    private String street;
    private int floorNumber;
    private String houseNumber;
    private String billingPerson;
    private String city;
    private String municipalityName;
    private float longitude;
    private float latitude;
    private Long zipCodeId;
    private int zipCodeValue;
    private String zipCodeName;

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
    private String locationName;
    private boolean updateHouseholdAddress;
    private boolean primary;


    public AddressDTO(Long id, String street, String houseNumber, String city, String municipalityName, Long zipCodeId, String regionName, String province, Long municipalityId) {
        this.id = id;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
        this.municipalityName = municipalityName;
        this.zipCodeId = zipCodeId;
        this.regionName = regionName;
        this.province = province;
        this.municipalityId = municipalityId;
    }

}
