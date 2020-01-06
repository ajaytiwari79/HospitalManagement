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
    private float longitude;
    private float latitude;

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
    private String locationName;
    private boolean updateHouseholdAddress;
    private boolean primary;
    private ZipCodeDTO zipCode;
    private MunicipalityDTO municipality;



}
