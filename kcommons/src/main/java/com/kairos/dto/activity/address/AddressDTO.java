package com.kairos.dto.activity.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by prabjot on 19/1/17.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO {
    private Long id;
    private String streetUrl;
    @NotEmpty(message = "error.ContactAddress.street.notnull") @NotNull(message = "error.ContactAddress.street.notnull")
    private String street1;
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

}
